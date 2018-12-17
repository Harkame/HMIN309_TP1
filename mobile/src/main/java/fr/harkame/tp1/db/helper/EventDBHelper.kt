package fr.harkame.tp1.db.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import fr.harkame.tp1.db.contract.DBContract
import fr.harkame.tp1.db.contract.EventType
import fr.harkame.tp1.db.model.EventModel
import org.joda.time.DateTime
import java.util.*

class EventDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object
    {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "event.db"

        private const val SQL_CREATE_TABLE =
                "CREATE TABLE " + DBContract.EventEntry.TABLE_NAME + " (" +
                        DBContract.EventEntry.COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.EventEntry.COLUMN_EVENT_NAME + " TEXT," +
                        DBContract.EventEntry.COLUMN_EVENT_DATE + " INTEGER ," +
                        DBContract.EventEntry.COLUMN_EVENT_DATE_TEXT + " TEXT ," +
                        DBContract.EventEntry.COLUMN_EVENT_TYPE + " INTEGER," +
                        DBContract.EventEntry.COLUMN_EVENT_DESCRIPTION + " NUMBER," +
                        DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION + " INTEGER)"

        private const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + DBContract.EventEntry.TABLE_NAME
    }

    override fun onCreate(database: SQLiteDatabase)
    {
        database.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        onUpgrade(database, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertEvent(event: EventModel): Boolean
    {
        removeOldEvents()

        val database = writableDatabase

        val values = ContentValues()
        values.put(DBContract.EventEntry.COLUMN_EVENT_NAME, event.name)
        values.put(DBContract.EventEntry.COLUMN_EVENT_DATE, event.date.millis)
        values.put(DBContract.EventEntry.COLUMN_EVENT_DATE_TEXT, event.dateText)
        values.put(DBContract.EventEntry.COLUMN_EVENT_TYPE, EventType.getIDFromType(event.type))
        values.put(DBContract.EventEntry.COLUMN_EVENT_DESCRIPTION, event.description)
        values.put(DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION, event.notification)

        database.insert(DBContract.EventEntry.TABLE_NAME, null, values)

        return true
    }

    fun readAllEvents(): ArrayList<EventModel>
    {
        removeOldEvents()

        val database = writableDatabase

        val dateOfTheDay = DateTime.now().withHourOfDay(0).millis

        val cursor: Cursor?
        try
        {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " WHERE event_date > " + dateOfTheDay + " ORDER BY " + DBContract.EventEntry.COLUMN_EVENT_DATE + " ASC", null)
        } catch (sqliteException : SQLiteException)
        {
            sqliteException.printStackTrace();
            return ArrayList()
        }

        return cursorToList(cursor)
    }

    fun updateNotification(eventId: Int, notification: Int)
    {
        val database = writableDatabase

        database.execSQL("UPDATE " + DBContract.EventEntry.TABLE_NAME + " SET " + DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION + " = " + notification + " WHERE " + DBContract.EventEntry.COLUMN_EVENT_ID + " = " + eventId)
    }

    fun readAllEventsForToday(): ArrayList<EventModel>
    {
        removeOldEvents()

        val database = writableDatabase

        val cursor: Cursor?

        try {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " WHERE " + DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION + " = 1 "+ " ORDER BY event_date ASC", null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }

        return cursorToList(cursor)
    }

    fun readAllEventsByNameOrByDate(typedText: CharSequence): ArrayList<EventModel> {
        val database = writableDatabase

        val cursor: Cursor?

        try
        {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " WHERE " + DBContract.EventEntry.COLUMN_EVENT_NAME + " LIKE  '" + typedText + "%' OR " + DBContract.EventEntry.COLUMN_EVENT_DATE_TEXT + " LIKE  '" + typedText + "%'", null)
        } catch (sqliteException: SQLiteException)
        {
            sqliteException.printStackTrace();
            return ArrayList()
        }

        return cursorToList(cursor)
    }

    private fun cursorToList(cursor: Cursor) : ArrayList<EventModel>{
        var eventId: Int
        var eventName: String
        var eventDate: Long
        var eventDateText : String
        var eventType: String
        var eventDescription: String
        var eventNotification: Boolean
        val events = ArrayList<EventModel>()

        val dateOfTheDay = DateTime.now().withHourOfDay(0).millis

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                eventId = cursor.getInt(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_ID))
                eventName = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_NAME))

                eventDate = cursor.getLong(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_DATE))

                eventDateText = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_DATE_TEXT))

                if(eventDate > dateOfTheDay) {
                    eventType = EventType.getTypeFromID(cursor.getInt(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_TYPE)))
                    eventDescription = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_DESCRIPTION))

                    eventNotification = cursor.getInt(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION)) == 1

                    events.add(EventModel(eventId, eventName, DateTime(eventDate), eventDateText, eventType, eventDescription, eventNotification))
                }
                cursor.moveToNext()
            }
        }
        return events
    }

    private fun removeOldEvents()
    {
        val database = writableDatabase

        database.rawQuery("DELETE FROM " + DBContract.EventEntry.TABLE_NAME + " WHERE " + DBContract.EventEntry.COLUMN_EVENT_NAME + " < " + DateTime.now().millis, null).close()
    }

    private fun resetDatabase()
    {
        val database = writableDatabase

        database.execSQL(SQL_DELETE_TABLE)
        database.execSQL(SQL_CREATE_TABLE)
    }
}