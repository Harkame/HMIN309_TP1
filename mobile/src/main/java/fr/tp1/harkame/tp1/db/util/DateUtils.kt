package fr.tp1.harkame.tp1

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {

    private const val DATE_FORMAT = "yyyy-MM-dd"

    @JvmStatic
    fun localDateToString(localDateTime: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

        return localDateTime.format(formatter)
    }

    @JvmStatic
    fun stringToLocalDate(eventDate: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

        return LocalDate.parse(eventDate, formatter)
    }
}