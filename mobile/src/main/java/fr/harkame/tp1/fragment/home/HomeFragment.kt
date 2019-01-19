package fr.harkame.tp1.fragment.home

import android.content.Context
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
import android.text.Editable
import android.text.TextWatcher
import android.widget.SearchView
import android.widget.TextView
import fr.harkame.tp1.activity.MainActivity

class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var homeEventAdapter: HomeEventAdapter

    private lateinit var currentContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")

        currentContext = context!!
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_home, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        eventDBHelper = EventDBHelper(this.context!!)

        val events = eventDBHelper.readAllEvents()

        homeEventAdapter = HomeEventAdapter(currentContext, events, this.activity as MainActivity)

        val eventList = view.findViewById<ListView>(R.id.list_events)
        eventList.adapter = homeEventAdapter

        val inputSearchView = view.findViewById<SearchView>(R.id.input_search)

        /*
        inputSearchView.addTextChangedListener(object : TextWatcher
        {
            override fun onTextChanged(charSequence: CharSequence, arg1: Int, arg2: Int, arg3: Int)
            {
                val events = eventDBHelper.readAllEventsByNameOrByDate(charSequence)

                homeEventAdapter = HomeEventAdapter(currentContext, events, activity as MainActivity)

                eventList.adapter = homeEventAdapter
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3 : Int)
            {
            }

            override fun afterTextChanged(arg0: Editable)
            {
            }
        })
        */

        inputSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                val events = eventDBHelper.readAllEventsByNameOrByDate(newText)

                homeEventAdapter = HomeEventAdapter(currentContext, events, activity as MainActivity)

                eventList.adapter = homeEventAdapter
                return true
            }
        })
    }
}
