package fr.harkame.tp1.db.util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object DateUtils {

    @JvmStatic
    fun dateTimeToString(dateTime: DateTime): String{
         return DateTimeFormat.forPattern("dd/MM/yyyy").print(dateTime)
    }
}