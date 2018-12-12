package com.myquotepro.myquotepro

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.myquotepro.myquotepro.payments.PaymentsActivity
import com.myquotepro.myquotepro.product.AddProductActivity
import com.myquotepro.myquotepro.product.QuotesActivity
import com.myquotepro.myquotepro.search.SearchActivity
import com.myquotepro.myquotepro.sessions.AccountActivity
import com.myquotepro.myquotepro.sessions.UserSession
import com.myquotepro.myquotepro.supplier.SuppliersActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

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

        // get user data from session
        val user = userSession.userDetails

        if (user[UserSession.KEY_USER_TYPE] == "Supplier") {
            fab.show()
            fab.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddProductActivity::class.java))
            }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val header = nav_view.getHeaderView(0)

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
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
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
            R.id.payments -> {
                startActivity(Intent(this@MainActivity, PaymentsActivity::class.java))
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
                        this@MainActivity,
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
            R.id.account -> {
                startActivity(Intent(this@MainActivity, AccountActivity::class.java))
            }
            else -> startActivity(Intent(this@MainActivity, EmptyActivity::class.java))
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 99
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val RESULT_OK = -1
        const val REQ_SCAN_RESULT = 200
        const val REQ_CODE_SPEECH_INPUT = 100
        internal var searchInProgress = false
    }

    class EnableInternetConnection : View.OnClickListener {
        override fun onClick(v: View) {
            android.Manifest.permission.INTERNET
        }
    }
}
