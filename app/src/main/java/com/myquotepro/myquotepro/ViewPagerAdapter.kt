package com.myquotepro.myquotepro

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

internal class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private val title = arrayOf(
        "Promotions",
        "Electronics",
        "Lifestyle",
        "Home Appliances",
        "Books & More",
        "Stationery",
        "Others"
    )

    override fun getItem(position: Int): Fragment {
        return TabFragment.getInstance(position, getPageTitle(position).toString())
    }

    override fun getCount(): Int {
        return title.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}