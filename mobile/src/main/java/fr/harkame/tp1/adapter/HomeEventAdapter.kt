package fr.harkame.tp1.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import fr.harkame.tp1.db.model.ViewHolder
import android.widget.CheckBox
import android.widget.TextView
import fr.harkame.tp1.R
import fr.harkame.tp1.db.model.EventModel
import fr.harkame.tp1.db.helper.EventDBHelper
import android.R.attr.fragment
import fr.harkame.tp1.activity.MainActivity


class HomeEventAdapter internal constructor(private val mycontext: Context, private val list: List<EventModel>, private val activity : MainActivity) : ArrayAdapter<EventModel>(mycontext,R.layout.event_row, list) {

    private var eventDBHelper = EventDBHelper(this.context)

    override fun getCount(): Int
    {
        return list.size
    }

    override fun getItem(position: Int): EventModel
    {
        return list[position]
    }

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }

    private fun isChecked(position: Int): Boolean
    {
        return list[position].notification
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        eventDBHelper = EventDBHelper(context)

        var rowView = convertView

        var viewHolder = ViewHolder()
        if (rowView == null) {
            val inflater = (context as Activity).layoutInflater
            rowView = inflater.inflate(R.layout.event_row, null)

            viewHolder.checkBox = rowView.findViewById(R.id.rowEventCheckBox) as CheckBox
            viewHolder.text = rowView.findViewById(R.id.rowEventText) as TextView
            rowView.tag = viewHolder
        } else {
            viewHolder = rowView.tag as ViewHolder
        }

        viewHolder.checkBox!!.isChecked = !list[position].notification

        val event = list[position]
        viewHolder.text!!.text = event.toString()

        viewHolder.checkBox!!.tag = position

        viewHolder.text!!.setOnClickListener{
            val eventModel = list[position]

            activity.pushDetailsFragment(eventModel)
        }

        viewHolder.checkBox!!.setOnClickListener {
            val newState = !list[position].notification
            list[position].notification = newState

            val eventModel = list[position]

            var notifictionActivated = 0

            if(eventModel.notification)
                notifictionActivated = 1

            eventDBHelper.updateNotification(eventModel.id, notifictionActivated)
        }

        viewHolder.checkBox!!.isChecked = isChecked(position)

        return rowView!!
    }
}