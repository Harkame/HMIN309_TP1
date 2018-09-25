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
import java.time.LocalDate


class EventDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertEvent(event: EventModel): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBContract.EventEntry.COLUMN_NAME, event.name)
        values.put(DBContract.EventEntry.COLUMN_DATE, DateUtils.localDateToString(event.date))
        values.put(DBContract.EventEntry.COLUMN_TYPE, event.type)
        values.put(DBContract.EventEntry.COLUMN_DESCRIPTION, event.description)

        db.insert(DBContract.EventEntry.TABLE_NAME, null, values)

        return true
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FeedReader.db"

        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.EventEntry.TABLE_NAME + " (" +
                        DBContract.EventEntry.COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.EventEntry.COLUMN_NAME + " TEXT," +
                        DBContract.EventEntry.COLUMN_DATE + " DATE," +
                        DBContract.EventEntry.COLUMN_TYPE + " TEXT," +
                        DBContract.EventEntry.COLUMN_DESCRIPTION + " TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.EventEntry.TABLE_NAME
    }

    fun readAllEvents(): ArrayList<EventModel> {
        val events = ArrayList<EventModel>()
        val db = writableDatabase

        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " ORDER BY " + DBContract.EventEntry.COLUMN_DATE + " ASC ", null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }

        return addEventsToList(cursor, events)
    }

    fun readAllEventsForToday(): ArrayList<EventModel> {
        var events = ArrayList<EventModel>()
        val db = writableDatabase
        val dateOfTheDay = LocalDate.now()

        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + "WHERE " + DBContract.EventEntry.COLUMN_DATE + " = " + dateOfTheDay + "ORDER BY " + DBContract.EventEntry.COLUMN_DATE + " ASC", null)
        } catch (e: SQLiteException){
            return ArrayList()
        }

        return addEventsToList(cursor, events);
    }

    private fun addEventsToList(cursor: Cursor, events: ArrayList<EventModel>): ArrayList<EventModel> {
        var eventName: String
        var eventDate: LocalDate
        var eventType: String
        var eventDescription: String

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                eventName = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_NAME))
                eventDate = DateUtils.stringToLocalDate(cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_DATE)))
                eventType = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_TYPE))
                eventDescription = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_TYPE))

                events.add(EventModel(eventName, eventDate, eventType, eventDescription))
                cursor.moveToNext()
            }
        }
        return events
    }
}