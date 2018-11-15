package com.myquotepro.myquotepro.search

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

class CouponsSearchContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {

        val queryType = ""
        when (uriMatcher.match(uri)) {
            1 -> {
                val query = uri.lastPathSegment!!.toLowerCase()
                return getSearchResultsCursor(query)
            }
            else -> return null
        }
    }

    private fun getSearchResultsCursor(searchString: String?): MatrixCursor {
        var searchString = searchString
        val searchResults = MatrixCursor(matrixCursorColumns)
        val mRow = arrayOfNulls<Any>(4)
        var counterId = 0
        if (searchString != null) {
            searchString = searchString.toLowerCase()

            for (rec in StoresData.getStores()!!) {
                if (rec.toLowerCase().contains(searchString)) {
                    mRow[0] = "" + counterId++
                    mRow[1] = rec

                    mRow[2] = context!!.resources.getIdentifier(
                        getStoreName(rec),
                        "drawable", context!!.packageName
                    )
                    mRow[3] = "" + counterId++

                    searchResults.addRow(mRow)
                }
            }
        }
        return searchResults
    }

    private fun getStoreName(suggestion: String): String {
        val suggestionWords = suggestion.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return suggestionWords[0].toLowerCase()
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        return 0
    }

    companion object {

        private val STORES = "stores/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*"

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI("com.myquotepro.myquotepro.search", STORES, 1)
        }

        private val matrixCursorColumns = arrayOf(
            "_id",
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_ICON_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA
        )
    }
}