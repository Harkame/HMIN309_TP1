package fr.tp1.harkame.tp1

import org.joda.time.DateTime
import java.io.Serializable

class EventModel : Serializable
{
    var id: Int = 0
    var name: String = ""
    var date: DateTime = DateTime.now()
    var type: String = ""
    var description: String = ""
    var notification: Boolean = false

    constructor(name: String, date: DateTime, type: String, description: String, notification: Boolean)
    {
        this.name = name
        this.date = date;
        this.type = type;
        this.description = description;
        this.notification = notification
    }

    constructor(id: Int, name: String, date: DateTime, type: String, description: String, notification: Boolean)
    {
        this.id = id;
        this.name = name
        this.date = date;
        this.type = type;
        this.description = description;
        this.notification = notification
    }

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