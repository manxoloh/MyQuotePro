package com.myquotepro.myquotepro.sessions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.myquotepro.myquotepro.LoginActivity
import java.util.*

class UserSession// Constructor
    (// Context
    private var _context: Context
) {
    // Shared Preferences
    private var pref: SharedPreferences

    // Editor for Shared preferences
    private var editor: Editor

    // Shared pref mode
    private var PRIVATE_MODE = 0


    /**
     * Get stored session data
     */
    // user name
    // user email id
    // return user
    val userDetails: HashMap<String, String>
        get() {
            val user = HashMap<String, String>()
            user[KEY_USERID] = pref.getString(KEY_USERID, null)
            user[KEY_USER_TYPE] = pref.getString(KEY_USER_TYPE, null)
            user[KEY_NAME] = pref.getString(KEY_NAME, null)
            user[KEY_EMAIL] = pref.getString(KEY_EMAIL, null)
            user[KEY_PHONE] = pref.getString(KEY_PHONE, null)
            user[KEY_PHONE] = pref.getString(KEY_PHONE, null)
            return user
        }

    /**
     * Quick check for login
     */
    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    /**
     * Create login session
     */
    fun createLoginSession(userId: String, userType: String, name: String, email: String, phone: String) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true)

        // Storing phone in pref
        editor.putString(KEY_USERID, userId)

        // Storing phone in pref
        editor.putString(KEY_USER_TYPE, userType)

        // Storing name in pref
        editor.putString(KEY_NAME, name)

        // Storing email in pref
        editor.putString(KEY_EMAIL, email)

        // Storing phone in pref
        editor.putString(KEY_PHONE, phone)

        // commit changes
        editor.commit()
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    fun checkLogin() {
        // Check login status
        if (!this.isLoggedIn) {
            // user is not logged in redirect him to Login Activity
            val i = Intent(_context, LoginActivity::class.java)
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Add new Flag to start new Activity
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Staring Login Activity
            _context.startActivity(i)
        }

    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()

        // After logout redirect user to Loing Activity
        val i = Intent(_context, LoginActivity::class.java)
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Add new Flag to start new Activity
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Staring Login Activity
        _context.startActivity(i)
    }

    companion object {

        // Sharedpref file name
        private val PREF_NAME = "MyQuotePro"

        // All Shared Preferences Keys
        private val IS_LOGIN = "IsLoggedIn"
        val KEY_NAME = "name"
        val KEY_USER_TYPE = "userType"
        val KEY_EMAIL = "email"
        val KEY_PHONE = "phone"
        val KEY_USERID = "userId"
    }
}