package com.myquotepro.myquotepro

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.customer.CustomerSignupActivity
import com.myquotepro.myquotepro.sessions.UserSession
import com.myquotepro.myquotepro.supplier.SupplierSignupActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    // Session Manager Class
    private lateinit var session: UserSession
    private var email: EditText? = null
    private var password: EditText? = null
    private var pd: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (UserSession(applicationContext).isLoggedIn) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        } else {
            pd = ProgressDialog(this@LoginActivity)
            // Get username, password from EditText
            email = findViewById(R.id.email)
            password = findViewById(R.id.password)

            //email_sign_in_button.setOnClickListener { attemptLogin() }
            supplier_sign_up.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SupplierSignupActivity::class.java))
            }
            customer_sign_up.setOnClickListener {
                startActivity(Intent(this@LoginActivity, CustomerSignupActivity::class.java))
            }

            // Session Manager
            session = UserSession(applicationContext)


            // Login button click event
            email_sign_in_button.setOnClickListener {
                val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

                if (!isConnected) {
                    val snackbar =
                        Snackbar.make(
                            findViewById(R.id.login_card_view),
                            "You have no internet connection",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Action", null)
                    snackbar.duration = 10000
                    snackbar.setAction(R.string.connect_to_internet, MainActivity.EnableInternetConnection())
                    snackbar.show()
                } else {
                    pd!!.setTitle("Authenticating")
                    pd!!.setMessage("Attempting to login...")

                    pd!!.show()

                    val queue = Volley.newRequestQueue(this@LoginActivity)
                    val url: String = "http://18.235.150.50/myquotepro/api/user/login"

                    val response: String? = null

                    val finalResponse = response

                    val postRequest = object : StringRequest(
                        Request.Method.POST, url,
                        Response.Listener<String> { response ->
                            pd!!.hide()
                            val jsonObject = JSONObject(response)
                            if (jsonObject.getString("success").toInt() == 1) {
                                val userData = JSONObject(jsonObject.getString("data"))

                                // Check if username, password is filled
                                session.createLoginSession(
                                    userData.getString("id"),
                                    userData.getString("usertype"),
                                    userData.getString("firstname") + " " + userData.getString("lastname"),
                                    userData.getString("email"),
                                    userData.getString("phone")
                                )

                                // Staring MainActivity
                                val i = Intent(applicationContext, MainActivity::class.java)
                                startActivity(i)
                                finish()

                                Toast.makeText(applicationContext, jsonObject.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Toast.makeText(applicationContext, jsonObject.getString("message"), Toast.LENGTH_LONG)
                                    .show()
                            }


                        },
                        Response.ErrorListener {
                            pd!!.hide()
                            Log.d("ErrorResponse", finalResponse)
                        }
                    ) {
                        override fun getParams(): Map<String, String> {
                            //Creating HashMap
                            val params = HashMap<String, String>()
                            params["username"] = email?.text.toString()
                            params["password"] = password?.text.toString()


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
    }
}
