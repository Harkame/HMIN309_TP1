package fr.harkame.tp1.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import fr.harkame.tp1.R
import fr.harkame.tp1.db.helper.EventDBHelper
import fr.harkame.tp1.db.model.Event
import fr.harkame.tp1.fragment.creation.EventCreationFragment
import fr.harkame.tp1.fragment.details.EventDetailsFragment
import fr.harkame.tp1.fragment.home.HomeFragment
import fr.harkame.tp1.fragment.voice.VoiceRecognition
import fr.harkame.tp1.service.wear.WearDataService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var eventDBHelper: EventDBHelper

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var fragment: Fragment

        when (item.itemId) {
            R.id.navigation_home -> {

                fragment = HomeFragment()

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_create -> {

                fragment = EventCreationFragment()

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_voice_recognition -> {
                fragment = VoiceRecognition()

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_main)

        eventDBHelper = EventDBHelper(this)

        startService(Intent(this, WearDataService::class.java))

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopService(Intent(this, WearDataService::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")

        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    fun pushDetailsFragment(event: Event) {
        val fragment = EventDetailsFragment.newInstance(event)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    fun popFragment() {
        supportFragmentManager.popBackStack()
    }
}
