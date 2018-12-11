package com.myquotepro.myquotepro.sessions

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.drawee.backends.pipeline.Fresco
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_product_details.*
import org.json.JSONArray

class AccountActivity : AppCompatActivity() {
    private var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_account)

        pd = ProgressDialog(this@AccountActivity)

        val userId = UserSession(applicationContext).userDetails[UserSession.KEY_USERID].toString()

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (!isConnected) {
            val snackbar =
                Snackbar.make(findViewById(R.id.linear), "You have no internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
            snackbar.duration = 10000
            snackbar.setAction(R.string.connect_to_internet, MainActivity.EnableInternetConnection())
            snackbar.show()
        } else {
            pd!!.setMessage("Loading ...")
            pd!!.show()

            val queue = Volley.newRequestQueue(this)
            val url: String = "http://18.235.150.50/myquotepro/api/user/account?user_id=$userId"

            // Request a string response from the provided URL.
            val productDetails = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    pd!!.hide()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        account_username.text = jsonArray.getJSONObject(i).getString("username")
                        email_address.text = jsonArray.getJSONObject(i).getString("email")
                        full_name.text = jsonArray.getJSONObject(i).getString("firstname") + " " +
                                jsonArray.getJSONObject(i).getString("lastname")
                        account_user_type.text = jsonArray.getJSONObject(i).getString("usertype")
//                        product_more_details.text = "\u2022" + " " + jsonArray.getJSONObject(i).getString("description")
//                        val featuredImage = findViewById<ImageView>(R.id.featured_image)
//                        Glide.with(this).load(jsonArray.getJSONObject(i).getString("feature_image")).into(featuredImage)
                    }
                },
                Response.ErrorListener {
                    pd!!.hide()
                })
            queue.add(productDetails)
        }
    }
}
