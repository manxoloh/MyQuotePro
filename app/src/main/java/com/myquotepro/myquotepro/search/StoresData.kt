package com.myquotepro.myquotepro.search

import java.util.*

object StoresData {
    private var stores: MutableList<String>? = null

    init {
        stores = ArrayList()
        stores!!.add("Amazon")
        stores!!.add("Sears")
        stores!!.add("Ebay Home")
        stores!!.add("Macys Home")
        stores!!.add("JCpenney Kids")
        stores!!.add("Ebay Electronics")
        stores!!.add("Amazon Appliance")
        stores!!.add("Ebay Mobiles")
        stores!!.add("Ebay Kids")
        stores!!.add("Amazon Fashion")
        stores!!.add("Ebay Travel")
        stores!!.add("JCpenney Home")
        stores!!.add("JCpenney Luggage")
        stores!!.add("JCpenney Appliance")
        stores!!.add("JCpenney Fashion")
        stores!!.add("Amazon Luggage")
        stores!!.add("Macys Jewellery")
        stores!!.add("JCpenney Jewellery")
        stores!!.add("Amazon Jewellery")
    }

    fun getStores(): List<String>? {
        return stores
    }

    fun filterData(searchString: String?): List<String> {
        var searchString = searchString
        val searchResults = ArrayList<String>()
        if (searchString != null) {
            searchString = searchString.toLowerCase()

            for (rec in stores!!) {
                if (rec.toLowerCase().contains(searchString)) {
                    searchResults.add(rec)
                }
            }
        }
        return searchResults
    }
}