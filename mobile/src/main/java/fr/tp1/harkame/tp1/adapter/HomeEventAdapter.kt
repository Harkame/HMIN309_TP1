package fr.tp1.harkame.tp1.adapter

import android.widget.Toast
import android.R.attr.checked
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.widget.TextView
import android.widget.CheckBox
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import fr.tp1.harkame.tp1.R
import fr.tp1.harkame.tp1.activity.Item
import fr.tp1.harkame.tp1.activity.ViewHolder
import kotlinx.android.synthetic.main.event_row.view.*


class HomeEventAdapter internal constructor(private val context: Context, private val list: List<Item>) : BaseAdapter() {

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
        return list[position].checked
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var rowView: View? = convertView

        // reuse views
        var viewHolder = ViewHolder()
        if (rowView == null) {
            val inflater = (context as Activity).layoutInflater
            rowView = inflater.inflate(R.layout.event_row, null)

            viewHolder.checkBox = rowView!!.findViewById(R.id.rowEventCheckBox)
            viewHolder.text = rowView.findViewById(R.id.rowEventText)
            rowView!!.setTag(viewHolder)
        } else {
            viewHolder = rowView.getTag() as ViewHolder
        }

        viewHolder.checkBox!!.setChecked(list[position].checked)

        val itemStr = list[position].text
        viewHolder.text!!.setText(itemStr)

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

        viewHolder.checkBox!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val newState = !list[position].isChecked()
                list[position].checked = newState
            }
        })

        viewHolder.checkBox!!.setChecked(isChecked(position))

        return rowView
    }
}