package fr.tp1.harkame.tp1.activity

import android.widget.TextView
import android.widget.CheckBox
import android.graphics.drawable.Drawable
import android.R.attr.checked




class Item internal constructor(internal var text: String, internal var checked: Boolean) {

    fun isChecked(): Boolean {
        return checked
    }
}

internal class ViewHolder {
    var checkBox: CheckBox? = null
    var text: TextView? = null
}