package com.myquotepro.myquotepro.search

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import com.myquotepro.myquotepro.product.ProductsModel
import com.myquotepro.myquotepro.product.ProductDetailsActivity
import kotlinx.android.synthetic.main.fragment_tab.*
import org.json.JSONArray
import java.util.*

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    // Declare Variables
    private var productsListView: ListView? = null
    private var products: SearchAdapter? = null
    private var editsearch: SearchView? = null
    private var pd: ProgressDialog? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        pd = ProgressDialog(this@SearchActivity)
        pd!!.setMessage("Processing ...")
        pd!!.show()

        val queue = Volley.newRequestQueue(this@SearchActivity)
        val url: String = "http://18.235.150.50/myquotepro/api/products/list?cat=1"

        // Request a string response from the provided URL.
        val productsRecords = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                pd!!.hide()
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    productsArrayList.add(
                        ProductsModel(
                            jsonArray.getJSONObject(i).getString("product_id"),
                            jsonArray.getJSONObject(i).getString("category_name"),
                            jsonArray.getJSONObject(i).getString("product_name"),
                            jsonArray.getJSONObject(i).getString("description"),
                            jsonArray.getJSONObject(i).getString("phone"),
                            "KES " + jsonArray.getJSONObject(i).getString("price"),
                            jsonArray.getJSONObject(i).getString("firstname") + " " + jsonArray.getJSONObject(i).getString(
                                "lastname"
                            ),
                            jsonArray.getJSONObject(i).getString("location"),
                            jsonArray.getJSONObject(i).getString("feature_image")
                        )
                    )
                }

                productsListView = findViewById<View>(R.id.products_search_list_view) as ListView
                products = SearchAdapter(this@SearchActivity)
                productsListView!!.adapter = products

                // Locate the EditText in listview_main.xml
                editsearch = findViewById<View>(R.id.search) as SearchView
                editsearch!!.setOnQueryTextListener(this)

                productsListView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val intent = Intent(this@SearchActivity, ProductDetailsActivity::class.java)
                    intent.putExtra("product_id", productsArrayList[position].product_id)
                    startActivity(intent)
                }

            }, Response.ErrorListener {
                pd!!.hide()
            })
        queue.add(productsRecords)
    }

    /**
     * Receiving speech input.
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param data        the data
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        MainActivity.searchInProgress = false

        if (resultCode == MainActivity.RESULT_OK && null != data) {
            when (requestCode) {
                MainActivity.REQ_CODE_SPEECH_INPUT -> {

                    val result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    audial_search_heading.text = "Search Results for " + result[0]
                }
                MainActivity.REQ_SCAN_RESULT -> {
                }
            }

        }

    }

    override fun onQueryTextSubmit(query: String): Boolean {

        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        products!!.filter(newText)
        return false
    }

    companion object {
        var productsArrayList = ArrayList<ProductsModel>()
    }
}