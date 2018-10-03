package fr.tp1.harkame.tp1

import java.text.SimpleDateFormat
import java.util.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat



object DateUtils {

    @JvmStatic
    fun dateTimeToString(dateTime: DateTime): String{
         return DateTimeFormat.forPattern("dd/MM/yyyy").print(dateTime)
    }

    @JvmStatic
    fun stringToDateTime(eventDate: String): DateTime {
        val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        return formatter.parseDateTime(eventDate)
    }
}