package com.myquotepro.myquotepro.product

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.drawee.backends.pipeline.Fresco
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import com.myquotepro.myquotepro.sessions.UserSession
import kotlinx.android.synthetic.main.activity_add_product.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException


class AddProductActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var productCategory = ""
    private var productName: EditText? = null
    private var description: EditText? = null
    private var stock: EditText? = null
    private var price: EditText? = null
    private var addProduct: Button? = null
    private var pd: SweetAlertDialog? = null
    internal lateinit var bitmap: Bitmap
    private val URL = "http://18.235.150.50/myquotepro/api/products/add-product"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_add_product)
        productName = findViewById(R.id.product_name)
        description = findViewById(R.id.description)
        stock = findViewById(R.id.stock)
        price = findViewById(R.id.price)
        addProduct = findViewById(R.id.add_product)

        val spinner: Spinner = findViewById(R.id.product_category)

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (!isConnected) {
            val snackbar =
                Snackbar.make(findViewById(R.id.user_account), "You have no internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
            snackbar.duration = 10000
            snackbar.setAction(R.string.connect_to_internet, MainActivity.EnableInternetConnection())
            snackbar.show()
        } else {
            val queue = Volley.newRequestQueue(this)
            val url: String = "http://18.235.150.50/myquotepro/api/products/categories"

            // Request a string response from the provided URL.
            val category = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val jsonArray = JSONArray(response)
                    // Spinner Drop down elements
                    val categories = ArrayList<String>()
                    for (i in 0 until jsonArray.length()) {
                        categories.add(jsonArray.getJSONObject(i).getString("category_name"))
                    }

                    // Creating adapter for spinner
                    val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    // attaching data adapter to spinner
                    spinner.adapter = dataAdapter

                    spinner.onItemSelectedListener = this

                },
                Response.ErrorListener { })
            queue.add(category)


            addProduct?.setOnClickListener {
                addProduct()
            }
        }

        featured_image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                MainActivity.PICK_IMAGE_REQUEST
            )
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        val selected = parent.getItemAtPosition(pos)
        productCategory = selected as String
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    private fun addProduct() {

        //Check whether there is active internet

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (!isConnected) {
            val snackbar =
                Snackbar.make(findViewById(R.id.user_account), "You have no internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
            snackbar.duration = 10000
            snackbar.setAction(R.string.connect_to_internet, MainActivity.EnableInternetConnection())
            snackbar.show()
        } else {
            pd = SweetAlertDialog(this@AddProductActivity, SweetAlertDialog.PROGRESS_TYPE)
            pd!!.contentText = "Processing ..."

            pd!!.show()

            val queue = Volley.newRequestQueue(this@AddProductActivity)

            val response: String? = null

            val finalResponse = response

            val postRequest = object : StringRequest(Request.Method.POST, URL,
                Response.Listener<String> { response ->
                    pd!!.hide()
                    val success = JSONObject(response).getString("success")
                    if (Integer.valueOf(success) == 1) {
                        pd = SweetAlertDialog(this@AddProductActivity, SweetAlertDialog.SUCCESS_TYPE)
                        pd!!.titleText = "Success"
                        pd!!.contentText = JSONObject(response).getString("message")
                        pd!!.show()
                    } else {
                        pd = SweetAlertDialog(this@AddProductActivity, SweetAlertDialog.ERROR_TYPE)
                        pd!!.titleText = "Error"
                        pd!!.contentText = JSONObject(response).getString("message")
                        pd!!.show()
                    }
                },
                Response.ErrorListener {
                    // error
                    pd!!.hide()
                    Log.d("ErrorResponse", finalResponse)
                }
            ) {
                override fun getParams(): Map<String, String> {
                    //Creating HashMap
                    val params = HashMap<String, String>()
                    // get user data from session
                    val user = UserSession(applicationContext).userDetails

                    params["product_category"] = productCategory
                    params["product_name"] = productName?.text.toString()
                    params["description"] = description?.text.toString()
                    params["price"] = price?.text.toString()
                    params["stock"] = stock?.text.toString()
                    params["supplier"] = user[UserSession.KEY_USERID].toString()
                    params["updated_by"] = user[UserSession.KEY_USERID].toString()
                    params["image"] = getStringImage(bitmap)

                    return params
                }
            }

            postRequest.retryPolicy = DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            queue.add(postRequest)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                Toast.makeText(this@AddProductActivity, "" + bitmap, Toast.LENGTH_SHORT).show()
                //Setting the Bitmap to ImageView
                featured_image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun getStringImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
}