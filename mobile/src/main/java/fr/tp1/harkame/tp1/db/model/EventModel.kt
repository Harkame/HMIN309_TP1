package fr.tp1.harkame.tp1

import org.joda.time.DateTime

class EventModel(val name: String, val date: DateTime, val type: String, val description: String, var notification: Boolean)
{
    override fun toString(): String {
        var eventToString = StringBuilder()

        eventToString.append(name)
        eventToString.append(" - ")
        eventToString.append(DateUtils.dateTimeToString(date))
        eventToString.append(" - ")
        eventToString.append(type)
        eventToString.append(" - ")
        eventToString.append(description)

        return eventToString.toString()
    }
}