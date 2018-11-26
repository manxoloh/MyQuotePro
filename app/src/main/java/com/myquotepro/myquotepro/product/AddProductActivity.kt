package com.myquotepro.myquotepro.product

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import com.myquotepro.myquotepro.sessions.UserSession
import org.json.JSONArray
import org.json.JSONObject


class AddProductActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var productCategory = ""
    private var productName: EditText? = null
    private var description: EditText? = null
    private var stock: EditText? = null
    private var price: EditText? = null
    private var addProduct: Button? = null
    private var pd: ProgressDialog? = null
    private val URL = "http://18.235.150.50/myquotepro/api/products/add-product"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        pd = ProgressDialog(this@AddProductActivity)
        productName = findViewById(R.id.product_name)
        description = findViewById(R.id.description)
        stock = findViewById(R.id.stock)
        price = findViewById(R.id.price)
        addProduct = findViewById(R.id.add_product)

        val spinner: Spinner = findViewById(R.id.product_category)

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
            pd!!.setMessage("Processing ...")

            pd!!.show()

            val queue = Volley.newRequestQueue(this@AddProductActivity)

            val response: String? = null

            val finalResponse = response

            val postRequest = object : StringRequest(Request.Method.POST, URL,
                Response.Listener<String> { response ->
                    pd!!.hide()
                    var success = JSONObject(response).getString("success")
                    if (Integer.valueOf(success) == 1) {
                        Toast.makeText(applicationContext, JSONObject(response).getString("message"), Toast.LENGTH_LONG)
                            .show()
                        startActivity(Intent(this@AddProductActivity, MainActivity::class.java))
                    } else {
                        Toast.makeText(applicationContext, JSONObject(response).getString("message"), Toast.LENGTH_LONG)
                            .show()
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

    //Method for clear Text After Sending Data to the server
    private fun remove() {
        productName?.text?.clear()
        description?.text?.clear()
        price?.text?.clear()
        stock?.text?.clear()
    }
}