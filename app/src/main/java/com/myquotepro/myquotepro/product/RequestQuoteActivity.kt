package com.myquotepro.myquotepro.product

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import com.myquotepro.myquotepro.sessions.UserSession
import org.json.JSONObject

class RequestQuoteActivity : AppCompatActivity() {
    private var quantity: EditText? = null
    private var unit_cost: EditText? = null
    private var total_amount: EditText? = null
    private var requestQuote: Button? = null
    private var pd: ProgressDialog? = null
    private val URL = "http://18.235.150.50/myquotepro/api/products/request-quote"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_quote)
        pd = ProgressDialog(this@RequestQuoteActivity)
        quantity = findViewById(R.id.quantity)
        unit_cost = findViewById(R.id.unit_cost)
        total_amount = findViewById(R.id.total_amount)
        requestQuote = findViewById(R.id.request_quote)

        requestQuote?.setOnClickListener {
            addProduct()
        }
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

            val queue = Volley.newRequestQueue(this@RequestQuoteActivity)

            val response: String? = null

            val finalResponse = response

            val postRequest = object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener<String> { response ->
                    pd!!.hide()
                    var success = JSONObject(response).getString("success")
                    if (Integer.valueOf(success) == 1) {
                        Toast.makeText(applicationContext, JSONObject(response).getString("message"), Toast.LENGTH_LONG)
                            .show()
                        startActivity(Intent(this@RequestQuoteActivity, MainActivity::class.java))
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

                    params["product_id"] = intent.getStringExtra("product_id")
                    params["quantity"] = quantity?.text.toString()
                    params["unit_cost"] = unit_cost?.text.toString()
                    params["total_amount"] = total_amount?.text.toString()
                    params["customer_id"] = user[UserSession.KEY_USERID].toString()

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
}
