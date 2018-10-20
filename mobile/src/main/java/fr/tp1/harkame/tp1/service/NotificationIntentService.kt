package fr.tp1.harkame.tp1.service

import android.content.Intent
import android.util.Log
import android.app.*
import android.support.v4.app.NotificationManagerCompat

class NotificationIntentService : IntentService("NotificationIntentService") {
    private val CHANNEL_ID = "eventChannel"
    var TAG = "Timers"

    val ACTION_DISMISS = "fr.tp1.harkame.tp1.service.NotificationIntentService.DISMISS"
    val ACTION_REPORT_SHORT = "fr.tp1.harkame.tp1.service.NotificationIntentService.5Minutes"
    val ACTION_REPORT_LONG = "fr.tp1.harkame.tp1.service.NotificationIntentService.1Hour"

    val NOTIFICATION_ID = 888

    val FIVE_MINUTES = 300L
    val HOUR = 3600000L

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent(): " + intent!!)

        val action = intent.action
        if (ACTION_DISMISS.equals(action)) {
            handleActionDismiss()
        } else if (ACTION_REPORT_SHORT.equals(action)) {
            handleActionReportShort()
        } else if (ACTION_REPORT_LONG.equals(action)) {
            handleActionReportLong()
    }
    }

    private fun handleActionDismiss() {
        Log.d(TAG, "handleActionDismiss()")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.cancel(NOTIFICATION_ID)
    }

    private fun handleActionReportShort() {
        Log.d(TAG, "handleActionSnooze()")

        val notification: Notification?
        notification = null

        if (notification != null) {
            val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

            notificationManagerCompat.cancel(NOTIFICATION_ID)

            try {
                Thread.sleep(FIVE_MINUTES)
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            notificationManagerCompat.notify(NOTIFICATION_ID, notification!!)
        }
    }

    private fun handleActionReportLong() {
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