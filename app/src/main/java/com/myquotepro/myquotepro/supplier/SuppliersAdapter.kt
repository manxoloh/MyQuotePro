package com.myquotepro.myquotepro.supplier

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myquotepro.myquotepro.R

class SuppliersAdapter(context: Context, supplier: ArrayList<SuppliersModel>) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(context)
    private val supplier: ArrayList<SuppliersModel> = supplier

    override fun getCount(): Int {
        return supplier.size
    }

    override fun getItem(position: Int): Any {
        return supplier[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val daftar: PaymentsRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.suppliers_listview, parent, false)
            daftar = PaymentsRowHolder(view)
            view.tag = daftar
        } else {
            view = convertView
            daftar = view.tag as PaymentsRowHolder
        }

        daftar.name.text = supplier[position].name
        daftar.status.text = supplier[position].status
        daftar.phone.text = supplier[position].phone
        daftar.email.text = supplier[position].email
        daftar.invoiceNumber.text = supplier[position].invoiceNumber
        daftar.transactionAmount.text = supplier[position].transactionAmount
        daftar.transactionCode.text = supplier[position].transactionCode
        return view
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