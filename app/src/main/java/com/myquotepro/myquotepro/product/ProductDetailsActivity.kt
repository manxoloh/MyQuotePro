package com.myquotepro.myquotepro.product

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.drawee.backends.pipeline.Fresco
import com.myquotepro.myquotepro.MainActivity
import com.myquotepro.myquotepro.R
import kotlinx.android.synthetic.main.activity_product_details.*
import org.json.JSONArray

class ProductDetailsActivity : AppCompatActivity() {
    private var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_product_details)

        pd = ProgressDialog(this@ProductDetailsActivity)

        val productId = intent.getStringExtra("product_id")

        pd!!.setMessage("Processing ...")
        pd!!.show()

        val queue = Volley.newRequestQueue(this)
        val url: String = "http://18.235.150.50/myquotepro/api/products/product-details?id=$productId"

        // Request a string response from the provided URL.
        val productDetails = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                pd!!.hide()
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    product_name.text = jsonArray.getJSONObject(i).getString("product_name")
                    product_price.text = "KES " + jsonArray.getJSONObject(i).getString("price")
                    delivery_terms.text = "FREE Delivery"
                    delivery_terms.text = "FREE Delivery"
                    product_status.text = "Available"
                    remaining_stock.text = jsonArray.getJSONObject(i).getString("stock") + " Pieces Remaining"
                    product_supplier.text = jsonArray.getJSONObject(i).getString("firstname") + " " +
                            jsonArray.getJSONObject(i).getString("lastname")
                    supplier_contact_phone.text = jsonArray.getJSONObject(i).getString("phone")
                    supplier_location.text = jsonArray.getJSONObject(i).getString("location")
                    product_more_details.text = "\u2022" + " " + jsonArray.getJSONObject(i).getString("description")

                    val supplierContactPhone = jsonArray.getJSONObject(i).getString("phone")
                    val supplierLocation = jsonArray.getJSONObject(i).getString("location")

                    layout_action2.setOnClickListener {

                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel: $supplierContactPhone")
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.CALL_PHONE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(android.Manifest.permission.CALL_PHONE),
                                MainActivity.LOCATION_PERMISSION_REQUEST_CODE
                            )
                        } else {
                            startActivity(intent)
                        }
                    }
                    layout_action3.setOnClickListener {
                        val intent = Intent(
                            android.content.Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=$supplierLocation")
                        )
                        startActivity(intent)
                    }
                    contact_supplier.setOnClickListener {

                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel: $supplierContactPhone")
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.CALL_PHONE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(android.Manifest.permission.CALL_PHONE),
                                MainActivity.LOCATION_PERMISSION_REQUEST_CODE
                            )
                        } else {
                            startActivity(intent)
                        }
                    }
                    request_quote.setOnClickListener {
                        pd!!.hide()
                        startActivity(Intent(this@ProductDetailsActivity, RequestQuoteActivity::class.java))
                    }
                }
            },
            Response.ErrorListener {
                pd!!.hide()
            })
        queue.add(productDetails)
    }
}
