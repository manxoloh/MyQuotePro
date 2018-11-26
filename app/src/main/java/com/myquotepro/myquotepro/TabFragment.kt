package com.myquotepro.myquotepro

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.myquotepro.myquotepro.adapters.ProductsAdapter
import com.myquotepro.myquotepro.models.ProductsModel
import com.myquotepro.myquotepro.product.ProductDetailsActivity
import org.json.JSONArray
import java.util.*


class TabFragment : Fragment() {

    internal var position: Int = 0
    internal var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments!!.getInt("pos") + 1
        title = arguments!!.getString("title")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue = Volley.newRequestQueue(activity)
        val url: String = "http://18.235.150.50/myquotepro/api/products/list?cat=$position"

        // Request a string response from the provided URL.
        val productsRecords = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
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

            }, Response.ErrorListener { })
        queue.add(productsRecords)

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