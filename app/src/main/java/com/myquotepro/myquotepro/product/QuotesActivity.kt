package com.myquotepro.myquotepro.product

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
import org.json.JSONArray

class QuotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.235.150.50/myquotepro/api/products/quotes?customer_id=1"

        // Request a string response from the provided URL.
        val requestedQuotes = StringRequest(
            Request.Method.GET, url,
            Listener<String> { response ->
                val jsonArray = JSONArray(response)
                val quote = ArrayList<ModelView>()
                for (i in 0 until jsonArray.length()) {
                    quote.add(
                        ModelView(
                            jsonArray.getJSONObject(i).getString("product_name"),
                            "Total: KES " + jsonArray.getJSONObject(i).getString("total_amount"),
                            jsonArray.getJSONObject(i).getString("status") + " @ " + jsonArray.getJSONObject(i).getString(
                                "updated_at"
                            ),
                            "Cost: KES " + jsonArray.getJSONObject(i).getString("unit_cost"),
                            "Quantity: " + jsonArray.getJSONObject(i).getString("quantity")
                        )
                    )
                }
                val quotes: AdapterListview
                val quotesListView = findViewById<ListView>(R.id.list_quotes)
                quotes = AdapterListview(applicationContext, quote)
                quotesListView.adapter = quotes

                quotesListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    Toast.makeText(
                        applicationContext, "$position selected", Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { })
        queue.add(requestedQuotes)
    }
}
