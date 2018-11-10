package fr.harkame.tp1.fragment.home

import android.os.Bundle
import android.util.Log
import fr.harkame.tp1.adapter.HomeEventAdapter
import fr.harkame.tp1.db.helper.EventDBHelper
import fr.harkame.tp1.R
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var homeEventAdapter: HomeEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the xml file for the fragment
        return inflater.inflate(R.layout.fragment_home, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventDBHelper = EventDBHelper(this.context!!)

        val events = eventDBHelper.readAllEvents()

        homeEventAdapter = HomeEventAdapter(this.context!!, events)

        val eventList = view.findViewById<ListView>(R.id.list_events)
        eventList.adapter = homeEventAdapter
    }
}
