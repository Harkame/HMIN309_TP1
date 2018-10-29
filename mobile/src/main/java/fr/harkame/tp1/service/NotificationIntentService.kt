package fr.harkame.tp1.service

import android.content.Intent
import android.util.Log
import android.app.*
import android.support.v4.app.NotificationManagerCompat
import com.github.jrejaud.wear_socket.WearSocket
import fr.harkame.tp1.db.model.EventModel
import fr.harkame.tp1.db.helper.EventDBHelper

class NotificationIntentService : IntentService("NotificationIntentService") {

    private lateinit var wearSocket : WearSocket

    var TAG = "Timers"

    companion object {
        const val ACTION_DISMISS = "fr.harkame.tp1.service.NotificationIntentService.DismissAction"
        const val ACTION_REPORT_SHORT = "fr.harkame.tp1.service.NotificationIntentService.ActionReportShort"
        const val ACTION_REPORT_LONG = "fr.harkame.tp1.service.NotificationIntentService.ActionReportLongAction"
        const val ACTION_START_SPORT_ACTIVITY = "fr.harkame.tp1.service.NotificationIntentService.ActionStartSportActivity"
    }


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
        } else if (ACTION_START_SPORT_ACTIVITY.equals(action)) {
            handleActionStartSportActivity(event, REPORT_TIME_LONG)
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

    private fun handleActionStartSportActivity(event : EventModel, reportTime : Long) {
        Log.d(TAG, "handleActionSnooze()")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        wearSocket = WearSocket.getInstance();

        wearSocket.setupAndConnect(this, "voice_transcription") {
            //Throws an error here if there is a problem connecting to the other device.
        }

        wearSocket.sendMessage("/start-activity","myMessage")

        Log.d(TAG, "Message sended")

        notificationManagerCompat.cancel(NOTIFICATION_ID)
    }
}