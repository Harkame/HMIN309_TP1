package fr.tp1.harkame.tp1.db.model

import fr.tp1.harkame.tp1.EventModel

class Item(internal var event: EventModel, internal var checked: Boolean) {

    fun isChecked(): Boolean {
        return checked
    }
}

