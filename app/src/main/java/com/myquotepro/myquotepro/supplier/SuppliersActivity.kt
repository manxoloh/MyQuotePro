package com.myquotepro.myquotepro.supplier

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import org.json.JSONArray

class SuppliersActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private var suppliersListView: ListView? = null
    private var suppliers: SuppliersAdapter? = null
    private var editsearch: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suppliers)

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (!isConnected) {
            val snackbar =
                Snackbar.make(
                    findViewById(R.id.suppliers_list),
                    "You have no internet connection",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Action", null)
            snackbar.duration = 10000
            snackbar.setAction(R.string.connect_to_internet, MainActivity.EnableInternetConnection())
            snackbar.show()
        } else {
            val queue = Volley.newRequestQueue(this)
            val url: String = "http://18.235.150.50/myquotepro/api/user/suppliers"

            // Request a string response from the provided URL.
            val suppliersRecords = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val jsonArray = JSONArray(response)
                    Log.e("Error", response)
                    for (i in 0 until jsonArray.length()) {
                        suppliersArrayList.add(
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

                    suppliersListView = findViewById<View>(R.id.suppliers_list) as ListView
                    suppliers = SuppliersAdapter(this@SuppliersActivity)
                    suppliersListView!!.adapter = suppliers

                    // Locate the EditText in listview_main.xml
                    editsearch = findViewById<View>(R.id.search) as SearchView
                    editsearch!!.setOnQueryTextListener(this)

                    suppliersListView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        Toast.makeText(
                            applicationContext,
                            suppliersArrayList[position].invoiceNumber, Toast.LENGTH_LONG
                        ).show()
                    }
                },
                Response.ErrorListener { })
            queue.add(suppliersRecords)
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        suppliers!!.filter(newText)
        return false
    }

    companion object {
        var suppliersArrayList = ArrayList<SuppliersModel>()
    }
}
