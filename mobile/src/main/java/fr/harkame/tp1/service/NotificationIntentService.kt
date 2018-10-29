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
        const val ACTION_DISMISS = "fr.harkame.service.NotificationIntentService.DismissAction"
        const val ACTION_REPORT_SHORT = "fr.harkame.service.NotificationIntentService.ActionReportShort"
        const val ACTION_REPORT_LONG = "fr.harkame.service.NotificationIntentService.ActionReportLongAction"
        const val ACTION_START_SPORT_ACTIVITY = "fr.harkame.service.NotificationIntentService.ActionStartSportActivity"
    }


    val REPORT_TIME_SHORT = 300000L
    val REPORT_TIME_LONG = 3600000L

    private lateinit var eventDBHelper: EventDBHelper

    override fun onHandleIntent(intent: Intent?) {

        eventDBHelper = EventDBHelper(this)

        Log.d(TAG, "onHandleIntent(): " + intent!!)

        var event = intent.getSerializableExtra("event") as EventModel
        var notificationID = intent.getIntExtra("notification_id", -1)

        val action = intent.action

        if (ACTION_DISMISS.equals(action))
            handleActionDismiss(event, notificationID)
        else if (ACTION_REPORT_SHORT.equals(action))
            handleActionReport(event, REPORT_TIME_SHORT, notificationID)
        else if (ACTION_REPORT_LONG.equals(action))
            handleActionReport(event, REPORT_TIME_LONG, notificationID)
        else if (ACTION_START_SPORT_ACTIVITY.equals(action))
            handleActionStartSportActivity(event, notificationID)
    }

    private fun handleActionDismiss(event : EventModel, notificationID : Int) {
        Log.d(TAG, "handleActionDismiss")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.cancel(notificationID)

        eventDBHelper.updateNotification(event.id, 0)
    }

    private fun handleActionReport(event : EventModel, reportTime : Long, notificationID : Int) {
        Log.d(TAG, "handleActionSnooze()")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        notificationManagerCompat.cancel(notificationID)

        try {
            Thread.sleep(reportTime)
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        //notificationManagerCompat.notify(NotificationService.NOTIFICATION_ID, notification!!)

    }

    private fun handleActionStartSportActivity(event : EventModel, notificationID : Int) {
        Log.d(TAG, "handleActionSnooze()")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        wearSocket = WearSocket.getInstance();

        wearSocket.setupAndConnect(this, "voice_transcription") {

        }

        Log.d(TAG, "Message sended")

        notificationManagerCompat.cancel(notificationID)

        //eventDBHelper.updateNotification(event.id, 0)

        wearSocket.sendMessage("/start-activity","")
    }
}