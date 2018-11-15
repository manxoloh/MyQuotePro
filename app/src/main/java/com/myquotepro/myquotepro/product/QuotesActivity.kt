package com.myquotepro.myquotepro.product

import android.content.Intent
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
import com.upkazi.hire.ModelView
import org.json.JSONArray

class QuotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qoutes)
        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.235.150.50/quotepro/api/products/list"

        // Request a string response from the provided URL.
        val requestedQuotes = StringRequest(
            Request.Method.GET, url,
            Listener<String> { response ->
                val jsonArray = JSONArray(response)
                val quote = ArrayList<ModelView>()
                for (i in 0 until jsonArray.length()) {
                    quote.add(
                        ModelView(
                            jsonArray.getJSONObject(i).getString("product_id"),
                            jsonArray.getJSONObject(i).getString("product_name"),
                            R.mipmap.ic_launcher,
                            jsonArray.getJSONObject(i).getString("description")
                        )
                    )
                }
                val quotes: AdapterListview
                val quotesListView = findViewById<ListView>(R.id.list_quotes)
                quotes = AdapterListview(applicationContext, quote)
                quotesListView.adapter = quotes

                quotesListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    productDetails(quote[position].list_id, quote[position].list_title)
                }

                Toast.makeText(
                    applicationContext,
                    "Fleet Cars Available For Booking", Toast.LENGTH_LONG
                ).show()
            },
            Response.ErrorListener { })
        queue.add(requestedQuotes)
    }


    private fun productDetails(list_id: String, list_title: String) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(list_id, list_title)
        startActivity(intent)
    }
}
