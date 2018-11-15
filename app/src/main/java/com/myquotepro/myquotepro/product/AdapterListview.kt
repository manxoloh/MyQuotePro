package com.myquotepro.myquotepro.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.myquotepro.myquotepro.R
import com.upkazi.hire.ModelView

class AdapterListview(context: Context, car: ArrayList<ModelView>) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(context)
    private val car: ArrayList<ModelView> = car

    override fun getCount(): Int {
        return car.size
    }

    override fun getItem(position: Int): Any {
        return car[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val daftar: ListRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.activity_listview, parent, false)
            daftar = ListRowHolder(view)
            view.tag = daftar
        } else {
            view = convertView
            daftar = view.tag as ListRowHolder
        }

        daftar.title_item.text = car[position].list_title
        daftar.desc_item.text = car[position].list_desc
        daftar.image_item.setImageResource(car[position].list_image)
        return view
    }
}

private class ListRowHolder(row: View?) {
    val title_item: TextView = row?.findViewById(R.id.title_listview) as TextView
    val desc_item: TextView = row?.findViewById(R.id.description_listview) as TextView
    val image_item: ImageView = row?.findViewById(R.id.image_listview) as ImageView

}