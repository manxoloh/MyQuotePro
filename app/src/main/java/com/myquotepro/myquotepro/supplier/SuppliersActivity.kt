package com.myquotepro.myquotepro.supplier

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.R
import org.json.JSONArray

class SuppliersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suppliers)

        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.235.150.50/myquotepro/api/user/suppliers"

        // Request a string response from the provided URL.
        val suppliersRecords = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                val jsonArray = JSONArray(response)
                Log.e("Error", response)
                val supplier = ArrayList<SuppliersModel>()
                for (i in 0 until jsonArray.length()) {
                    supplier.add(
                        SuppliersModel(
                            jsonArray.getJSONObject(i).getString("firstname") + " " + jsonArray.getJSONObject(
                                i
                            ).getString("lastname"),
                            "VERIFIED",
                            jsonArray.getJSONObject(i).getString("phone"),
                            jsonArray.getJSONObject(i).getString("email"),
                            "Reg. Certificate",
                            "VAT Certificate",
                            "Trade License"
                        )
                    )
                }
                val suppliers: SuppliersAdapter
                val suppliersListView = findViewById<ListView>(R.id.suppliers_list)
                suppliers = SuppliersAdapter(applicationContext, supplier)
                suppliersListView.adapter = suppliers

                suppliersListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    Toast.makeText(
                        applicationContext,
                        supplier[position].invoiceNumber, Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { })
        queue.add(suppliersRecords)
    }
}
