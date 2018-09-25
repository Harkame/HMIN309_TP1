package fr.tp1.harkame.tp1.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import fr.tp1.harkame.tp1.R
import fr.tp1.harkame.tp1.db.model.Item
import fr.tp1.harkame.tp1.db.model.ViewHolder
import android.widget.CheckBox
import android.widget.TextView
import fr.tp1.harkame.tp1.EventModel


class HomeEventAdapter internal constructor(private val context: Context, private val list: List<EventModel>) : BaseAdapter() {

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

        /*
            viewHolder.checkBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    list.get(position).checked = b;

                    Toast.makeText(getApplicationContext(),
                            itemStr + "onCheckedChanged\nchecked: " + b,
                            Toast.LENGTH_LONG).show();
                }
            });
            */

        viewHolder.checkBox!!.setOnClickListener(View.OnClickListener {
            val newState = !list[position].notification//.isChecked()
            list[position].notification = newState

            var event = list[position]
        })

        viewHolder.checkBox!!.setChecked(isChecked(position))

        return rowView!!
    }
}