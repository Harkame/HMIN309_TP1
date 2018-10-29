package fr.harkame.tp1.activity.home

import android.app.Notification
import android.app.NotificationChannel
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.NotificationCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import fr.harkame.tp1.adapter.HomeEventAdapter
import fr.harkame.tp1.db.helper.EventDBHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import fr.harkame.tp1.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.ListView
import fr.harkame.tp1.activity.creation.EventCreationActivity
import fr.harkame.tp1.service.NotificationService


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    companion object {
        private const val TAG = "HomeActivity"
        private const val START_ACTIVITY_PATH = "/start-activity"
    }
    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var homeEventAdapter : HomeEventAdapter

    private lateinit var mGoogleApiClient: GoogleApiClient

    private val CHANNEL_ID = "HMIN309TP1HomeActivityChannel"

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

        startService(Intent(this, NotificationService::class.java))





        /*
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        mGoogleApiClient!!.connect()

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(object : ResultCallback<NodeApi.GetConnectedNodesResult> {
            override fun onResult(getConnectedNodesResult: NodeApi.GetConnectedNodesResult) {
                for (node in getConnectedNodesResult.nodes) {
                    sendMessage(node.id)

                    Log.d(TAG, node.toString())
                }
                mGoogleApiClient.disconnect()
            }
        })
        */
    }

    /*
    private fun sendMessage(node: String) {
        val charset = Charsets.UTF_8
        val byteArray = "Hello".toByteArray(charset)

        Wearable.getMessageClient(this).sendMessage(node, START_ACTIVITY_PATH, byteArray).apply {
            addOnSuccessListener { Log.d("HomeActivity : ", "Message sended") }
            addOnFailureListener { Log.e("HomeActivity : ", "Fail to send message") }
        }
    }
    */

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

    override fun onConnected(p0: Bundle?) {
        Log.d(TAG, "onConneted:")
    }



    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, "onConnectionSuspended: $i")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed: $connectionResult")
    }
}
