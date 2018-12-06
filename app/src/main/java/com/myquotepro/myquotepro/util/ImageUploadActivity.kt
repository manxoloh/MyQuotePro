package com.myquotepro.myquotepro.util

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ImageUploadActivity : AppCompatActivity() {

    private lateinit var upload: Button
    internal lateinit var imageview: ImageView
    internal lateinit var bitmap: Bitmap
    private var pd: ProgressDialog? = null
    internal var apiUrl = "http://18.235.150.50/myquotepro/api/user/image-upload"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload)
        pd = ProgressDialog(this@ImageUploadActivity)
        upload = findViewById<View>(R.id.upload) as Button
        imageview = findViewById<View>(R.id.imageview) as ImageView
        //  bitmap = BitmapFactory.decodeResource(getResources(), R.id.imageview);

        imageview.setOnClickListener { showFileChooser() }

        upload.setOnClickListener { uploaduserimage() }


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission()
            }
        }

    }


    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(this, " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@ImageUploadActivity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@ImageUploadActivity, "Permission Granted Successfully! ", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@ImageUploadActivity, "Permission Denied 🙁 ", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                Toast.makeText(this@ImageUploadActivity, "" + bitmap, Toast.LENGTH_SHORT).show()
                //Setting the Bitmap to ImageView
                imageview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    fun uploaduserimage() {//Check whether there is active internet

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

            val queue = Volley.newRequestQueue(this@ImageUploadActivity)

            val response: String? = null

            val finalResponse = response

            val postRequest = object : StringRequest(
                Request.Method.POST, apiUrl,
                Response.Listener<String> { response ->
                    pd!!.hide()
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG)
                        .show()
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

    fun getStringImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    companion object {
        private val PERMISSION_REQUEST_CODE = 1
        val PICK_IMAGE_REQUEST = 99
    }


}