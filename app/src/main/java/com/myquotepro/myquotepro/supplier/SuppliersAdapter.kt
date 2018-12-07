package com.myquotepro.myquotepro.supplier

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myquotepro.myquotepro.R
import java.util.*

class SuppliersAdapter(context: Context) : BaseAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private val arrayList: ArrayList<SuppliersModel> = ArrayList()

    init {
        this.arrayList.addAll(SuppliersActivity.suppliersArrayList)
    }

    override fun getCount(): Int {
        return SuppliersActivity.suppliersArrayList.size
    }

    override fun getItem(position: Int): SuppliersModel {
        return SuppliersActivity.suppliersArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val daftar: PaymentsRowHolder
        if (convertView == null) {
            view = this.inflater.inflate(R.layout.suppliers_listview, parent, false)
            daftar = PaymentsRowHolder(view)
            view.tag = daftar
        } else {
            view = convertView
            daftar = view.tag as PaymentsRowHolder
        }
        // Set the results into TextViews
        daftar.name.text = arrayList[position].name
        daftar.status.text = arrayList[position].status
        daftar.phone.text = arrayList[position].phone
        daftar.email.text = arrayList[position].email
        daftar.invoiceNumber.text = arrayList[position].invoiceNumber
        daftar.transactionAmount.text = arrayList[position].transactionAmount
        daftar.transactionCode.text = arrayList[position].transactionCode
        return view
    }

    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        SuppliersActivity.suppliersArrayList.clear()
        if (charText.isEmpty()) {
            SuppliersActivity.suppliersArrayList.addAll(arrayList)
        } else {
            for (supplier in arrayList) {
                if (supplier.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    SuppliersActivity.suppliersArrayList.add(supplier)
                }
            }
        }
        notifyDataSetChanged()
    }

}

private class PaymentsRowHolder(row: View?) {
    val name: TextView = row?.findViewById(R.id.transaction_description) as TextView
    val status: TextView = row?.findViewById(R.id.transaction_status) as TextView
    val phone: TextView = row?.findViewById(R.id.transaction_date) as TextView
    val email: TextView = row?.findViewById(R.id.payee_name) as TextView
    val invoiceNumber: TextView = row?.findViewById(R.id.invoice_number) as TextView
    val transactionAmount: TextView = row?.findViewById(R.id.transaction_amount) as TextView
    val transactionCode: TextView = row?.findViewById(R.id.transaction_code) as TextView

}