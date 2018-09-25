package fr.tp1.harkame.tp1.db.model

import android.widget.CheckBox
import android.widget.TextView

class Item(internal var text: String, internal var checked: Boolean) {

    fun isChecked(): Boolean {
        return checked
    }
}

