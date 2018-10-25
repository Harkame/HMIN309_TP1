package fr.harkame.tp1.service

import android.content.Intent
import android.util.Log
import android.app.*
import android.support.v4.app.NotificationManagerCompat
import fr.harkame.tp1.db.model.EventModel
import fr.harkame.tp1.db.helper.EventDBHelper

class NotificationIntentService : IntentService("NotificationIntentService") {
    private val CHANNEL_ID = "eventChannel"
    var TAG = "Timers"

    val ACTION_DISMISS = "fr.harkame.tp1.service.NotificationIntentService.DismissAction"
    val ACTION_REPORT_SHORT = "fr.harkame.tp1.service.NotificationIntentService.ActionReportShort"
    val ACTION_REPORT_LONG = "fr.harkame.tp1.service.NotificationIntentService.ActionReportLongAction"

    val REPORT_TIME_SHORT = 300000L
    val REPORT_TIME_LONG = 3600000L

    val NOTIFICATION_ID = 888

    val FIVE_MINUTES = 300L
    val HOUR = 3600000L

    private lateinit var eventDBHelper: EventDBHelper

    override fun onHandleIntent(intent: Intent?) {

        eventDBHelper = EventDBHelper(this)

        Log.d(TAG, "onHandleIntent(): " + intent!!)

        var event = intent.getSerializableExtra("event") as EventModel

        val action = intent.action
        if (ACTION_DISMISS.equals(action)) {
            handleActionDismiss(event)
        } else if (ACTION_REPORT_SHORT.equals(action)) {
            handleActionReport(event, REPORT_TIME_SHORT)
        } else if (ACTION_REPORT_LONG.equals(action)) {
            handleActionReport(event, REPORT_TIME_LONG)
        }
    }

    private fun handleActionDismiss(event : EventModel) {
        Log.d(TAG, "handleActionDismiss()")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.cancel(NOTIFICATION_ID)

        eventDBHelper.updateNotification(event.id, 0)
    }

    private fun handleActionReport(event : EventModel, reportTime : Long) {
        Log.d(TAG, "handleActionSnooze()")

        val notification: Notification?
        notification = null

        if (notification != null) {
            val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

            notificationManagerCompat.cancel(NOTIFICATION_ID)

            try {
                Thread.sleep(HOUR)
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            notificationManagerCompat.notify(NOTIFICATION_ID, notification!!)
        }
    }
}