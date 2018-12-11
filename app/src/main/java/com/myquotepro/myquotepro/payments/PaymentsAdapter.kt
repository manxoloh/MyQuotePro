package com.myquotepro.myquotepro.payments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myquotepro.myquotepro.R

class PaymentsAdapter(context: Context, payment: ArrayList<PaymentsModel>) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(context)
    private val payment: ArrayList<PaymentsModel> = payment

    override fun getCount(): Int {
        return payment.size
    }

    override fun getItem(position: Int): Any {
        return payment[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val daftar: PaymentsRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.payments_listview, parent, false)
            daftar = PaymentsRowHolder(view)
            view.tag = daftar
        } else {
            view = convertView
            daftar = view.tag as PaymentsRowHolder
        }

        daftar.transactionTitle.text = payment[position].transactionTitle
        daftar.transactionDescription.text = payment[position].transactionDescription
        daftar.transactionStatus.text = payment[position].transactionStatus
        daftar.transactionAmount.text = payment[position].transactionAmount
        daftar.transactionCode.text = payment[position].transactionCode
        daftar.transactionDate.text = payment[position].transactionDate
        return view
    }
}

private class PaymentsRowHolder(row: View?) {
    val transactionTitle: TextView = row?.findViewById(R.id.transaction_title) as TextView
    val transactionDescription: TextView = row?.findViewById(R.id.transaction_description) as TextView
    val transactionStatus: TextView = row?.findViewById(R.id.transaction_status) as TextView
    val transactionAmount: TextView = row?.findViewById(R.id.transaction_amount) as TextView
    val transactionCode: TextView = row?.findViewById(R.id.transaction_code) as TextView
    val transactionDate: TextView = row?.findViewById(R.id.transaction_date) as TextView

}