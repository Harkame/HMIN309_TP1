package fr.harkame.tp1.service.notification

import android.content.Intent
import android.util.Log
import android.app.*
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import fr.harkame.tp1.db.model.EventModel
import fr.harkame.tp1.db.helper.EventDBHelper
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.NodeApi
import fr.harkame.tp1.R
import fr.harkame.tp1.activity.MainActivity
import fr.harkame.tp1.db.contract.EventType


class NotificationIntentService : IntentService("NotificationIntentService")
{
    companion object {
        const val TAG = "NotificationIntentServi" //23char

        const val ACTION_DISMISS = "fr.harkame.service.NotificationIntentService.DismissAction"
        const val ACTION_REPORT_SHORT = "fr.harkame.service.NotificationIntentService.ActionReportShort"
        const val ACTION_REPORT_LONG = "fr.harkame.service.NotificationIntentService.ActionReportLongAction"
        const val ACTION_START_SPORT_ACTIVITY = "fr.harkame.service.NotificationIntentService.ActionStartSportActivity"

        private const val START_ACTIVITY_PATH = "/start-activity"

        private const val CHANNEL_ID = "notify_001"
    }


    val REPORT_TIME_SHORT = 300000L
    val REPORT_TIME_LONG = 3600000L

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var mNode: Node

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
        Log.d(TAG, "handleActionReport")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        notificationManagerCompat.cancel(notificationID)

        try {
            Thread.sleep(reportTime)
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        notificationManagerCompat.notify(notificationID, createNotification(event, notificationID))
    }

    private fun handleActionStartSportActivity(event : EventModel, notificationID : Int) {
        Log.d(TAG, "handleActionStartSportActivity")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        notificationManagerCompat.cancel(notificationID)

        eventDBHelper.updateNotification(event.id, 0)

        resolveNode()
    }

    private fun resolveNode() {
        Log.d(TAG, "resolveNode")

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build()

        mGoogleApiClient.connect()

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(object : ResultCallback<NodeApi.GetConnectedNodesResult> {
                    override fun onResult(connectedNodes: NodeApi.GetConnectedNodesResult) {
                        for (connectedNode in connectedNodes.nodes) {
                            mNode = connectedNode
                            sendMessage(START_ACTIVITY_PATH, "")
                        }
                    }
                })
    }


    private fun sendMessage(subject: String, message: String) {
        Log.d(TAG, "sendMessage")

        Wearable.MessageApi.sendMessage(mGoogleApiClient,
                mNode.id,
                subject,
                message.toByteArray())
                .setResultCallback(object : ResultCallback<MessageApi.SendMessageResult> {
                    override fun onResult(sendMessageResult: MessageApi.SendMessageResult) {
                        if (sendMessageResult.status.isSuccess)
                            Log.d(TAG, "Message sended")
                        else
                            Log.e(TAG, "Message not sended")
                    }
                })
    }

    fun createNotification(event: EventModel, notificationID: Int) : Notification
    {
        Log.d(TAG, "createNotification")

        var intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val dismissIntent = Intent(this, NotificationIntentService::class.java)
        dismissIntent.action = NotificationIntentService.ACTION_DISMISS

        dismissIntent.putExtra("event", event)
        dismissIntent.putExtra("notification_id", notificationID)

        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)
        val dismissAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_check,
                "Ok",
                dismissPendingIntent)
                .build()


        val reportShortIntent = Intent(this, NotificationIntentService::class.java)
        reportShortIntent.action = ACTION_REPORT_SHORT

        reportShortIntent.putExtra("event", event)
        reportShortIntent.putExtra("notification_id", notificationID)

        val reportShortPendingIntent = PendingIntent.getService(this, 0, reportShortIntent, 0)

        val reportShortAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "Report : 5 minutes",
                reportShortPendingIntent)
                .build()

        val reportLongIntent = Intent(this, NotificationIntentService::class.java)
        reportLongIntent.action = NotificationIntentService.ACTION_REPORT_LONG

        reportLongIntent.putExtra("event", event)
        reportLongIntent.putExtra("notification_id", notificationID)

        val reportLongPendingIntentService = PendingIntent.getService(this, 0, reportLongIntent, 0)
        val reportlongAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "Report : 1 heure",
                reportLongPendingIntentService)
                .build()

        val notificationCompatBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)

        var buildedNotification = notificationCompatBuilder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Evenement Aujourd'hui : " + event.name)
                .setContentText(event.description)
                .setChannelId(CHANNEL_ID)
                .addAction(dismissAction)
                .addAction(reportShortAction)
                .addAction(reportlongAction)

        if(event.type == EventType.eventTypes[0])
        {
            val startSportActivityIntent = Intent(this, NotificationIntentService::class.java)
            startSportActivityIntent.action = NotificationIntentService.ACTION_START_SPORT_ACTIVITY

            startSportActivityIntent.putExtra("event", event)
            startSportActivityIntent.putExtra("notification_id", notificationID)

            val startSportActivityPendingIntentService = PendingIntent.getService(this, 0, startSportActivityIntent, 0)
            val startSportActivitygAction = android.support.v4.app.NotificationCompat.Action.Builder(
                    R.drawable.ic_alarm,
                    "Commencer activité sportive",
                    startSportActivityPendingIntentService)
                    .build()

            buildedNotification.addAction(startSportActivitygAction)
        }

        return buildedNotification.build()

    }
}