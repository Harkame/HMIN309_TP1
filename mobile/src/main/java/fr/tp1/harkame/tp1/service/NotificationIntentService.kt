package fr.tp1.harkame.tp1.service

import android.content.Intent
import android.util.Log
import android.app.*
import android.support.v4.app.NotificationManagerCompat
import fr.tp1.harkame.tp1.EventModel

class NotificationIntentService : IntentService("NotificationIntentService") {
    private val CHANNEL_ID = "eventChannel"
    var TAG = "Timers"

    val ACTION_DISMISS = "fr.tp1.harkame.tp1.service.NotificationIntentService.Dismiss"
    val ACTION_REPORT_SHORT = "fr.tp1.harkame.tp1.service.NotificationIntentService.ActionReportShort"
    val ACTION_REPORT_LONG = "fr.tp1.harkame.tp1.service.NotificationIntentService.ActionReportLong"

    val REPORT_TIME_SHORT = 300000L
    val REPORT_TIME_LONG = 3600000L

    val NOTIFICATION_ID = 888

    val FIVE_MINUTES = 300L
    val HOUR = 3600000L

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent(): " + intent!!)

        var event = intent.getSerializableExtra("event") as EventModel

        val action = intent.action
        if (ACTION_DISMISS.equals(action)) {
            handleActionDismiss()
        } else if (ACTION_REPORT_SHORT.equals(action)) {
            handleActionReport(event, REPORT_TIME_SHORT)
        } else if (ACTION_REPORT_LONG.equals(action)) {
            handleActionReport(event, REPORT_TIME_LONG)
        }
    }

    private fun handleActionDismiss() {
        Log.d(TAG, "handleActionDismiss()")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.cancel(NOTIFICATION_ID)
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