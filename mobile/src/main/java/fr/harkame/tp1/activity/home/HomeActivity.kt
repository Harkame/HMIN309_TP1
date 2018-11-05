package fr.harkame.tp1.activity.home

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import fr.harkame.tp1.adapter.HomeEventAdapter
import fr.harkame.tp1.db.helper.EventDBHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.google.android.gms.common.api.GoogleApiClient
import fr.harkame.tp1.R
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.ListView
import fr.harkame.tp1.activity.creation.EventCreationActivity
import fr.harkame.tp1.service.notification.NotificationService
import fr.harkame.tp1.service.wear.WearDataService

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "HomeActivity"
    }

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var homeEventAdapter: HomeEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            val intent = Intent(this, EventCreationActivity::class.java).apply {

            }
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        eventDBHelper = EventDBHelper(this)

        val events = eventDBHelper.readAllEvents()

        homeEventAdapter = HomeEventAdapter(this, events)

        val eventList = findViewById<ListView>(R.id.list_events)
        eventList.adapter = homeEventAdapter

        nav_view.setNavigationItemSelectedListener(this)

        startService(Intent(this, NotificationService::class.java))

        startService(Intent(this, WearDataService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()

        stopService(Intent(this, NotificationService::class.java))

        stopService(Intent(this, WearDataService::class.java))
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")

        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected")

        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onNavigationItemSelected")

        when (item.itemId) {

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
