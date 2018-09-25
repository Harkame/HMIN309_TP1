package fr.tp1.harkame.tp1

import java.time.LocalDate

class EventModel(val name: String, val date: LocalDate, val type: String, val description: String, var notification: Boolean)
{
    override fun toString(): String {
        var eventToString = StringBuilder()

        eventToString.append(name)
        eventToString.append(" - ")
        eventToString.append(date)
        eventToString.append(" - ")
        eventToString.append(type)
        eventToString.append(" - ")
        eventToString.append(description)
        eventToString.append(" - ")

        return eventToString.toString()
    }
}