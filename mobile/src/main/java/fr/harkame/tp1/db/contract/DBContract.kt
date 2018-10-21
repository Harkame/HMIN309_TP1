package fr.harkame.tp1.db.contract

import android.provider.BaseColumns

object DBContract {
    class EventEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "events"
            const val COLUMN_EVENT_ID = "event_id"
            const val COLUMN_EVENT_NAME = "event_name"
            const val COLUMN_EVENT_DATE = "event_date"
            const val COLUMN_EVENT_TYPE = "event_type"
            const val COLUMN_EVENT_DESCRIPTION = "event_description"
            const val COLUMN_EVENT_NOTIFICATION = "event_notification"
        }
    }
}