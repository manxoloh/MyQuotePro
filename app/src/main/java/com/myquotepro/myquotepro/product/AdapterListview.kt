package com.myquotepro.myquotepro.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myquotepro.myquotepro.R

class AdapterListview(context: Context, quote: ArrayList<ModelView>) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(context)
    private val quote: ArrayList<ModelView> = quote

    override fun getCount(): Int {
        return quote.size
    }

    override fun getItem(position: Int): Any {
        return quote[position]
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

        daftar.quoteTitle.text = quote[position].list_title
        daftar.status.text = quote[position].status
        daftar.amountPaid.text = quote[position].amount_paid
        daftar.unitCost.text = quote[position].unit_cost
        daftar.quantity.text = quote[position].quantity
        return view
    }
}

private class ListRowHolder(row: View?) {
    val quoteTitle: TextView = row?.findViewById(R.id.quote_title) as TextView
    val status: TextView = row?.findViewById(R.id.status) as TextView
    val amountPaid: TextView = row?.findViewById(R.id.total_amount) as TextView
    val unitCost: TextView = row?.findViewById(R.id.unit_cost) as TextView
    val quantity: TextView = row?.findViewById(R.id.quantity) as TextView

}