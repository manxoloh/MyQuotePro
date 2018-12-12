package com.myquotepro.myquotepro.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.myquotepro.myquotepro.R


class ProductsAdapter(context: Context, product: ArrayList<ProductsModel>) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(context)
    private val product: ArrayList<ProductsModel> = product

    override fun getCount(): Int {
        return product.size
    }

    override fun getItem(position: Int): Any {
        return product[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val daftar: ProductRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.products_listview, parent, false)
            daftar = ProductRowHolder(view)
            view.tag = daftar
        } else {
            view = convertView
            daftar = view.tag as ProductRowHolder
        }

        daftar.productId.text = product[position].product_id
        daftar.category.text = product[position].category
        daftar.title.text = product[position].title
        daftar.phone.text = product[position].phone
        daftar.description.text = product[position].description
        daftar.price.text = product[position].price
        daftar.supplier.text = product[position].supplier
        daftar.location.text = product[position].location
        val image = daftar.featuredImage
        Glide.with(parent).load(product[position].featured_image).into(image)

        return view
    }

}

private class ProductRowHolder(row: View?) {
    val productId: TextView = row?.findViewById(R.id.item_id) as TextView
    val category: TextView = row?.findViewById(R.id.item_category) as TextView
    val title: TextView = row?.findViewById(R.id.item_name) as TextView
    val phone: TextView = row?.findViewById(R.id.item_supplier_phone) as TextView
    val description: TextView = row?.findViewById(R.id.item_short_desc) as TextView
    val price: TextView = row?.findViewById(R.id.item_price) as TextView
    val supplier: TextView = row?.findViewById(R.id.item_supplier) as TextView
    val location: TextView = row?.findViewById(R.id.item_location) as TextView
    val image: ImageView = row?.findViewById(R.id.product_thumb) as ImageView
    val featuredImage: ImageView = row?.findViewById(R.id.product_thumb) as ImageView

}