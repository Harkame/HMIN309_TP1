package fr.harkame.tp1.fragment.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Button
import fr.harkame.tp1.R
import fr.harkame.tp1.db.helper.EventDBHelper
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View


class SearchFragment : Fragment() {

    companion object {
        private const val TAG = "SearchFragment"
    }

    private lateinit var eventDBHelper : EventDBHelper

    private lateinit var buttonType : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the xml file for the fragment
        return inflater.inflate(R.layout.fragment_search, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}
