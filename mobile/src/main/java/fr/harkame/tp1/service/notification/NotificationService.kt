package fr.harkame.tp1.service.notification

import java.util.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.app.*
import android.os.Handler
import fr.harkame.tp1.db.helper.EventDBHelper
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import fr.harkame.tp1.R
import fr.harkame.tp1.activity.MainActivity
import fr.harkame.tp1.db.contract.EventType
import fr.harkame.tp1.db.model.EventModel
import fr.harkame.tp1.service.notification.NotificationIntentService.Companion.ACTION_REPORT_SHORT

class NotificationService : Service() {
    companion object {
        private const val TAG = "NotificationService"
        private const val CHANNEL_ID = "notify_001"

        private var NOTIFICATION_ID = 0
    }

    private val DEFAULT_TIME_START_NOTIFICATION = 10L

    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var handler : Handler

    private lateinit var eventDBHelper: EventDBHelper
    private lateinit var mNotificationManager: NotificationManagerCompat

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d(TAG, "onStartCommand")

        startTimer()

        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")

        mNotificationManager = NotificationManagerCompat.from(applicationContext)

        eventDBHelper = EventDBHelper(this)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        stoptimertask()

        super.onDestroy()
    }

    fun startTimer() {
        Log.d(TAG, "startTimer")

        timer = Timer()

        initializeTimerTask()

        timer.schedule(
                timerTask,
                DEFAULT_TIME_START_NOTIFICATION,
                DEFAULT_TIME_START_NOTIFICATION * 30000
        )
    }

    fun stoptimertask()
    {
        Log.d(TAG, "stoptimertask")

        timer.cancel()
    }

    fun initializeTimerTask() {
        Log.d(TAG, "initializeTimerTask")

        handler = Handler()
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post{
                    sendNotifications()
                }
            }
        }
    }

    fun sendNotifications() {
        Log.d(TAG, "sendNotifications")

        val events = eventDBHelper.readAllEventsForToday()

        for (event in events)
        {
            mNotificationManager.notify(NOTIFICATION_ID, createNotification(event))

            NOTIFICATION_ID++
        }
    }

    fun createNotification(event: EventModel) : Notification
    {
        Log.d(TAG, "createNotification")

        var intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val dismissIntent = Intent(this, NotificationIntentService::class.java)
        dismissIntent.action = NotificationIntentService.ACTION_DISMISS

        dismissIntent.putExtra("event", event)
        dismissIntent.putExtra("notification_id", NOTIFICATION_ID)

        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)
        val dismissAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_check,
                "Ok",
                dismissPendingIntent)
                .build()


        val reportShortIntent = Intent(this, NotificationIntentService::class.java)
        reportShortIntent.action = ACTION_REPORT_SHORT

        reportShortIntent.putExtra("event", event)
        reportShortIntent.putExtra("notification_id", NOTIFICATION_ID)

        val reportShortPendingIntent = PendingIntent.getService(this, 0, reportShortIntent, 0)

        val reportShortAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "Report : 5 minutes",
                reportShortPendingIntent)
                .build()

        val reportLongIntent = Intent(this, NotificationIntentService::class.java)
        reportLongIntent.action = NotificationIntentService.ACTION_REPORT_LONG

        reportLongIntent.putExtra("event", event)
        reportLongIntent.putExtra("notification_id", NOTIFICATION_ID)

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
            startSportActivityIntent.putExtra("notification_id", NOTIFICATION_ID)

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