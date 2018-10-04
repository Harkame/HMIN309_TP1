package fr.tp1.harkame.tp1.db.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import fr.tp1.harkame.tp1.DBContract
import fr.tp1.harkame.tp1.DateUtils
import fr.tp1.harkame.tp1.EventModel
import org.joda.time.DateTime
import java.util.*


class EventDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "event.db"

        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.EventEntry.TABLE_NAME + " (" +
                        DBContract.EventEntry.COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.EventEntry.COLUMN_NAME + " TEXT," +
                        DBContract.EventEntry.COLUMN_DATE + " TEXT," +
                        DBContract.EventEntry.COLUMN_TYPE + " TEXT," +
                        DBContract.EventEntry.COLUMN_DESCRIPTION + " TEXT," +
                        DBContract.EventEntry.COLUMN_NOTIFICATION + " INTEGER)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.EventEntry.TABLE_NAME
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(database, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertEvent(event: EventModel): Boolean {
        val database = writableDatabase

        val values = ContentValues()
        values.put(DBContract.EventEntry.COLUMN_NAME, event.name)
        values.put(DBContract.EventEntry.COLUMN_DATE, DateUtils.dateTimeToString(event.date))
        values.put(DBContract.EventEntry.COLUMN_TYPE, event.type)
        values.put(DBContract.EventEntry.COLUMN_DESCRIPTION, event.description)
        values.put(DBContract.EventEntry.COLUMN_NOTIFICATION, event.notification)

        database.insert(DBContract.EventEntry.TABLE_NAME, null, values)

        return true
    }

    fun readAllEvents(): ArrayList<EventModel> {
        val database = writableDatabase

        var cursor: Cursor?
        try {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " ORDER BY " + DBContract.EventEntry.COLUMN_DATE + " ASC ", null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }

        return cursorToList(cursor)
    }

    fun updateNotification(eventId: Int, notification: Int){
        val database = writableDatabase

        database.execSQL("UPDATE " + DBContract.EventEntry.TABLE_NAME + " SET " + DBContract.EventEntry.COLUMN_NOTIFICATION + " = " + notification + "WHERE " + DBContract.EventEntry.COLUMN_EVENT_ID + " = " + eventId)
    }

    fun readAllEventsForToday(): ArrayList<EventModel> {
        val database = writableDatabase

        val dateOfTheDay = DateTime.now()

        var cursor: Cursor?

        try {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " WHERE event_date = '" + DateUtils.dateTimeToString(dateOfTheDay) + "' ORDER BY event_date ASC", null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }

        return cursorToList(cursor)
    }

    private fun cursorToList(cursor: Cursor) : ArrayList<EventModel>{
        var eventName: String
        var eventDate: DateTime
        var eventType: String
        var eventDescription: String
        var eventNotification: Boolean
        val events = ArrayList<EventModel>()

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                eventName = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_NAME))
                eventDate = DateUtils.stringToDateTime(cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_DATE)))
                eventType = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_TYPE))
                eventDescription = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_DESCRIPTION))

                eventNotification = cursor.getInt(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_NOTIFICATION)) == 0

                events.add(EventModel(eventName, eventDate, eventType, eventDescription, eventNotification))
                cursor.moveToNext()
            }
        }
        return events
    }

    private fun resetDatabase()
    {
        val database = writableDatabase

        database.execSQL(SQL_DELETE_ENTRIES)
        database.execSQL(SQL_CREATE_ENTRIES)
    }
}