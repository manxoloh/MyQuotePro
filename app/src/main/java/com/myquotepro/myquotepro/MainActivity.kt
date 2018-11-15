package com.myquotepro.myquotepro

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.myquotepro.myquotepro.product.AddProductActivity
import com.myquotepro.myquotepro.product.QuotesActivity
import com.myquotepro.myquotepro.sessions.UserSession
import com.myquotepro.myquotepro.supplier.SuppliersActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.viewpager)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager!!.adapter = adapter

        tabLayout = findViewById(R.id.tabs)
        tabLayout!!.setupWithViewPager(viewPager)

        // Session class instance
        val userSession = UserSession(applicationContext)
        userSession.checkLogin()

        fab.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddProductActivity::class.java))
        }
        global_search.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchResultActivity::class.java))
        }
        audial_search.setOnClickListener {
            promptSpeechInput()
        }
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val header = nav_view.getHeaderView(0)
        // get user data from session
        val user = userSession.userDetails
        header.profile_username.text = user[UserSession.KEY_NAME]
        header.profile_email.text = user[UserSession.KEY_EMAIL]
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this@MainActivity, SearchResultActivity::class.java))
                true
            }
            R.id.action_logout -> {
                UserSession(applicationContext).logoutUser()
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.suppliers -> {
                startActivity(Intent(this@MainActivity, SuppliersActivity::class.java))
            }
            R.id.quotes -> {
                startActivity(Intent(this@MainActivity, QuotesActivity::class.java))
            }
            R.id.help_center -> {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel: 0704400725")
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CALL_PHONE), MainActivity.LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    startActivity(intent)
                }
            }
            R.id.share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/maithya.kavyu")
                    type = "text/plain"
                }
                startActivity(sendIntent)
            }
            else -> startActivity(Intent(this@MainActivity, EmptyActivity::class.java))
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private val RESULT_OK = -1
        private val REQ_SCAN_RESULT = 200
        private const val REQ_CODE_SPEECH_INPUT = 100
        internal var searchInProgress = false
    }

    class EnableInternetConnection : View.OnClickListener {
        override fun onClick(v: View) {
            android.Manifest.permission.INTERNET
        }
    }

    /**
     * Showing google speech input dialog.
     */
    private fun promptSpeechInput() {

        searchInProgress = true

        audial_search_heading.text = "Deal of the day"
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What are you looking for?")
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(this@MainActivity, "Voice search not supported by your device ", Toast.LENGTH_SHORT).show()
        }

    }
    git add --all
    git commit -am "First commit"
    git push
    /**
     * Receiving speech input.
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param data        the data
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        searchInProgress = false

        if (resultCode == RESULT_OK && null != data) {
            when (requestCode) {
                REQ_CODE_SPEECH_INPUT -> {

                    val result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    audial_search_heading.text = "Search Results for " + result[0]
                }
                REQ_SCAN_RESULT -> {
                }
            }

        }

    }
}
