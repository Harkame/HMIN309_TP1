package fr.harkame.tp1.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import fr.harkame.tp1.db.model.ViewHolder
import android.widget.CheckBox
import android.widget.TextView
import fr.harkame.tp1.R
import fr.harkame.tp1.db.model.EventModel
import fr.harkame.tp1.db.helper.EventDBHelper


class HomeEventAdapter internal constructor(private val context: Context, private val list: List<EventModel>) : BaseAdapter() {

    private lateinit var eventDBHelper: EventDBHelper

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun isChecked(position: Int): Boolean {
        return list[position].notification
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        eventDBHelper = EventDBHelper(context)

        var rowView = convertView

        // reuse views
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

        viewHolder.checkBox!!.setChecked(!list[position].notification)

        val event = list[position]
        viewHolder.text!!.setText(event.toString())

        viewHolder.checkBox!!.setTag(position)

        viewHolder.checkBox!!.setOnClickListener {
            val newState = !list[position].notification
            list[position].notification = newState

            var event = list[position]

            var notifictionActivated = 0

            if(event.notification)
                notifictionActivated = 1

            eventDBHelper.updateNotification(event.id, notifictionActivated)
        }

        viewHolder.checkBox!!.setChecked(isChecked(position))

        return rowView!!
    }
}