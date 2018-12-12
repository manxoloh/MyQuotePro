package com.myquotepro.myquotepro.payments

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.R
import com.myquotepro.myquotepro.sessions.UserSession
import org.json.JSONArray

class PaymentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)

        val queue = Volley.newRequestQueue(this)
        val url: String =
            "http://18.235.150.50/myquotepro/api/user/payments?payee_id=" + UserSession(applicationContext).userDetails[UserSession.KEY_USERID]

        // Request a string response from the provided URL.
        val paymentsRecords = StringRequest(Request.Method.GET, url,
            Listener<String> { response ->
                val jsonArray = JSONArray(response)
                val payment = ArrayList<PaymentsModel>()
                for (i in 0 until jsonArray.length()) {
                    payment.add(
                        PaymentsModel(
                            jsonArray.getJSONObject(i).getString("payment_title"),
                            jsonArray.getJSONObject(i).getString("payment_description"),
                            jsonArray.getJSONObject(i).getString("payment_status"),
                            jsonArray.getJSONObject(i).getString("payment_method"),
                            "Ref: " + jsonArray.getJSONObject(i).getString("reference_token"),
                            "Amount: KES " + jsonArray.getJSONObject(i).getString("amount_paid"),
                            jsonArray.getJSONObject(i).getString("transaction_date")
                        )
                    )
                }
                val payments: PaymentsAdapter
                val paymentsListView = findViewById<ListView>(R.id.payment_list)
                payments = PaymentsAdapter(applicationContext, payment)
                paymentsListView.adapter = payments

                paymentsListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    Toast.makeText(
                        applicationContext,
                        payment[position].transactionCode, Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { })
        queue.add(paymentsRecords)
    }
}
