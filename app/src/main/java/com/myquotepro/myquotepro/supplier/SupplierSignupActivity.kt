package com.myquotepro.myquotepro.supplier

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import com.myquotepro.myquotepro.mpesa.ApiClient
import com.myquotepro.myquotepro.mpesa.model.AccessToken
import com.myquotepro.myquotepro.mpesa.model.STKPush
import com.myquotepro.myquotepro.util.AppConstants.*
import com.myquotepro.myquotepro.util.SharedPrefsUtil
import com.myquotepro.myquotepro.util.Utils
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class SupplierSignupActivity : AppCompatActivity() {

    private var username: EditText? = null
    private var password: EditText? = null
    private var firstname: EditText? = null
    private var lastname: EditText? = null
    private var email: EditText? = null
    private var phone: EditText? = null
    private var location: EditText? = null
    internal lateinit var image: CircleImageView
    private lateinit var upload: ImageView
    private var profile: String? = null
    private var imageStatus = false
    private lateinit var profilePicture: Bitmap
    private var usertype = "Supplier"
    private var signup: Button? = null
    private var pd: ProgressDialog? = null
    private val URL = "http://18.235.150.50/myquotepro/api/user/signup"


    private var mSharedPrefsUtil: SharedPrefsUtil? = null
    private var mApiClient: ApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_signup)
        pd = ProgressDialog(this@SupplierSignupActivity)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        firstname = findViewById(R.id.first_name)
        lastname = findViewById(R.id.last_name)
        email = findViewById(R.id.email)
        location = findViewById(R.id.location)
        phone = findViewById(R.id.phone)
        signup = findViewById(R.id.signup)
        upload = findViewById(R.id.upload_photo)
        image = findViewById(R.id.profile_picture)

        signup?.setOnClickListener {


            //result will be available in onActivityResult which is overridden
            mSharedPrefsUtil = SharedPrefsUtil(this)
            mApiClient = ApiClient()
            mApiClient!!.setIsDebug(true) //Set True to enable logging, false to disable.

            getAccessToken()
            userSignup()
        }

        upload.setOnClickListener { view ->
            Dexter.withActivity(this@SupplierSignupActivity)
                .withPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = "image/*"
                            startActivityForResult(intent, 1000)
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // permission is denied permenantly, navigate user to app settings
                            Snackbar.make(view, "Kindly grant Required Permission", Snackbar.LENGTH_LONG)
                                .setAction("Allow", null).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                })
                .onSameThread()
                .check()
        }

    }

    private fun userSignup() {

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

            val queue = Volley.newRequestQueue(this@SupplierSignupActivity)

            val response: String? = null

            val finalResponse = response

            val postRequest = object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener<String> { response ->
                    pd!!.hide()
                    val success = JSONObject(response).getString("success")
                    if (Integer.valueOf(success) == 1) {
                        makePayment(JSONObject(response).getString("user"))
                        //performSTKPush(phone?.text.toString())
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

                    params["username"] = username?.text.toString()
                    params["password"] = password?.text.toString()
                    params["firstname"] = firstname?.text.toString()
                    params["lastname"] = lastname?.text.toString()
                    params["email"] = email?.text.toString()
                    params["phone"] = phone?.text.toString()
                    params["location"] = location?.text.toString()
                    params["usertype"] = usertype

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


    private fun convertBitmapToString(profilePicture: Bitmap) {
        /*
                Base64 encoding requires a byte array, the bitmap image cannot be converted directly into a byte array.
                so first convert the bitmap image into a ByteArrayOutputStream and then convert this stream into a byte array.
            */
        val byteArrayOutputStream = ByteArrayOutputStream()
        profilePicture.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
        val array = byteArrayOutputStream.toByteArray()
        profile = Base64.encodeToString(array, Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            //Image Successfully Selected
            try {
                //parsing the Intent data and displaying it in the imageview
                val imageUri = data.data//Geting uri of the data
                val imageStream = contentResolver.openInputStream(imageUri!!)//creating an imputstrea
                profilePicture = BitmapFactory.decodeStream(imageStream)//decoding the input stream to bitmap
                image.setImageBitmap(profilePicture)
                imageStatus = true//setting the flag
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
    }

    private fun getAccessToken() {
        mApiClient!!.setGetAccessToken(true)
        mApiClient!!.mpesaService().accessToken.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: retrofit2.Response<AccessToken>) {

                if (response.isSuccessful) {
                    mApiClient!!.setAuthToken(response.body()!!.accessToken)
                }
            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {

            }
        })
    }

    fun makePayment(payee_id: String) {

        pd!!.setMessage("Processing Payment...")
        pd!!.show()

        val requestQueue = Volley.newRequestQueue(this@SupplierSignupActivity)
        val url: String = "http://18.235.150.50/myquotepro/api/user/payment?payee_id=$payee_id"

        // Request a string response from the provided URL.
        val requestQuote = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { requestResponse ->
                pd!!.hide()
                Toast.makeText(applicationContext, JSONObject(requestResponse).getString("message"), Toast.LENGTH_LONG)
                    .show()
            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
                pd!!.hide()
            })
        requestQueue.add(requestQuote)
    }


    fun performSTKPush(phone_number: String) {
        pd!!.setMessage("Launching MPESA Menu")
        pd!!.isIndeterminate = true
        pd!!.show()

        val timestamp = Utils.getTimestamp()
        val stkPush = STKPush(
            BUSINESS_SHORT_CODE,
            Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
            timestamp,
            TRANSACTION_TYPE,
            "10",
            Utils.sanitizePhoneNumber(phone_number),
            PARTYB,
            Utils.sanitizePhoneNumber(phone_number),
            CALLBACKURL,
            "MyQuotePro", //The account reference
            "MyQuotePro Registration Fee"  //The transaction description
        )

        mApiClient!!.setGetAccessToken(false)

        mApiClient!!.mpesaService().sendPush(stkPush).enqueue(object : Callback<STKPush> {
            override fun onResponse(call: Call<STKPush>, response: retrofit2.Response<STKPush>) {
                pd!!.hide()
                try {
                    if (response.isSuccessful) {
                        Timber.d("post submitted to API. %s", response.body())
                    } else {
                        Timber.e("Response %s", response.errorBody()!!.string())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<STKPush>, t: Throwable) {
                pd!!.dismiss()
                Timber.e(t)
            }
        })
    }
}
