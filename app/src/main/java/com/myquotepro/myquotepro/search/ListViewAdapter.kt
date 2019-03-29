package com.myquotepro.myquotepro.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myquotepro.myquotepro.R

import java.util.ArrayList
import java.util.Locale

class ListViewAdapter(
    // Declare Variables

    internal var mContext: Context
) : BaseAdapter() {
    internal var inflater: LayoutInflater
    private val arraylist: ArrayList<MovieNames>

    init {
        inflater = LayoutInflater.from(mContext)
        this.arraylist = ArrayList()
        this.arraylist.addAll(SearchActivity.movieNamesArrayList)
    }

    inner class ViewHolder {
        internal var name: TextView? = null
    }

    override fun getCount(): Int {
        return SearchActivity.movieNamesArrayList.size
    }

    override fun getItem(position: Int): MovieNames {
        return SearchActivity.movieNamesArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        if (view == null) {
            holder = ViewHolder()
            view = inflater.inflate(R.layout.listview_item, null)
            // Locate the TextViews in listview_item.xml
            holder.name = view!!.findViewById<View>(R.id.name) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        // Set the results into TextViews
        holder.name!!.text = SearchActivity.movieNamesArrayList[position].animalName
        return view
    }

    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        SearchActivity.movieNamesArrayList.clear()
        if (charText.length == 0) {
            SearchActivity.movieNamesArrayList.addAll(arraylist)
        } else {
            for (wp in arraylist) {
                if (wp.animalName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    SearchActivity.movieNamesArrayList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

}