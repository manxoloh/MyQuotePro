package com.myquotepro.myquotepro

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.product.ProductDetailsActivity
import com.myquotepro.myquotepro.product.ProductsAdapter
import com.myquotepro.myquotepro.product.ProductsModel
import com.myquotepro.myquotepro.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_tab.*
import org.json.JSONArray
import java.util.*


class TabFragment : Fragment() {

    internal var position: Int = 0
    internal var title: String? = null
    private var pd: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments!!.getInt("pos") + 1
        title = arguments!!.getString("title")
        pd = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pd!!.contentText = "Loading"
        pd!!.show()
        val queue = Volley.newRequestQueue(activity)
        val url: String = "http://18.235.150.50/myquotepro/api/products/list?cat=$position"

        // Request a string response from the provided URL.
        val productsRecords = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                pd!!.hide()
                val jsonArray = JSONArray(response)
                val product = ArrayList<ProductsModel>()

                for (i in 0 until jsonArray.length()) {
                    product.add(
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
                if (product.isEmpty()) {
                    val notFound = view.findViewById<TextView>(R.id.audial_search_heading)
                    notFound.visibility = View.VISIBLE
                    notFound.text = "No products found under $title"
                }

                val products: ProductsAdapter
                val productsListView = view.findViewById<ListView>(R.id.products_list_view)
                products = ProductsAdapter(context!!, product)
                productsListView.adapter = products

                productsListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val intent = Intent(activity, ProductDetailsActivity::class.java)
                    intent.putExtra("product_id", product[position].product_id)
                    startActivity(intent)
                }

            }, Response.ErrorListener {
                pd!!.hide()
            })
        queue.add(productsRecords)

        global_search.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
        audial_search.setOnClickListener {
            promptSpeechInput()
        }
    }

    /**
     * Showing google speech input dialog.
     */
    private fun promptSpeechInput() {

        MainActivity.searchInProgress = true

        audial_search_heading.text = "Deal of the day"
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What are you looking for?")
        try {
            startActivityForResult(intent, MainActivity.REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(activity, "Voice search not supported by your device ", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Receiving speech input.
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param data        the data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

    companion object {

        fun getInstance(position: Int, title: String): Fragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            bundle.putString("title", title)
            val tabFragment = TabFragment()
            tabFragment.arguments = bundle
            return tabFragment
        }
    }
}