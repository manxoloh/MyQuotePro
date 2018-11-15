package com.myquotepro.myquotepro

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.myquotepro.myquotepro.search.CustomSearchAdapter
import com.myquotepro.myquotepro.search.StoresData
import kotlinx.android.synthetic.main.activity_search_result.*


class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val listView = findViewById<ListView>(R.id.listView)
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            //execution come here when an item is clicked from
            //the search results displayed after search form is submitted
            //you can continue from here with user clicked search item
            Toast.makeText(
                this@SearchResultActivity,
                "clicked search result item is" + (view as TextView).text,
                Toast.LENGTH_SHORT
            ).show()
        }
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.getItem(0)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )
        searchView.isFocusable = true
        searchItem.expandActionView()
        return true
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val searchQuery = intent.getStringExtra(SearchManager.QUERY)

            val adapter = CustomSearchAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                StoresData.filterData(searchQuery)
            )
            listView.adapter = adapter

        } else if (Intent.ACTION_VIEW == intent.action) {
            val selectedSuggestionRowId = intent.dataString
            //execution comes here when an item is selected from search suggestions
            //you can continue from here with user selected search item
            Toast.makeText(
                this, "selected search suggestion $selectedSuggestionRowId",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
