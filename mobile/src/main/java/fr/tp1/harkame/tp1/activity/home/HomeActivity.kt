package fr.tp1.harkame.tp1.activity.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import fr.tp1.harkame.tp1.db.helper.EventDBHelper
import fr.tp1.harkame.tp1.R
import fr.tp1.harkame.tp1.adapter.HomeEventAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import fr.tp1.harkame.tp1.service.NotificationService
import fr.tp1.harkame.tp1.activity.creation.EventCreationActivity
import fr.tp1.harkame.tp1.service.BridgeNotificationService


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var homeEventAdapter : HomeEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
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

        //startService(Intent(this,NotificationService::class.java))

        //startService(Intent(this,BridgeNotificationService::class.java))
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


}
