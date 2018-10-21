package fr.harkame.tp1.service

import java.util.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.app.*
import android.os.Handler
import fr.harkame.tp1.db.helper.EventDBHelper
import fr.tp1.harkame.tp1.R
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import fr.harkame.tp1.activity.home.HomeActivity
import fr.harkame.tp1.db.model.EventModel

class NotificationService : Service() {
    val ACTION_DISMISS = "fr.harkame.tp1.service.NotificationIntentService.DismissAction"
    val ACTION_REPORT_SHORT = "fr.harkame.tp1.service.NotificationIntentService.ActionReportShort"
    val ACTION_REPORT_LONG = "fr.harkame.tp1.service.NotificationIntentService.ActionReportLongAction"

    private val DEFAULT_TIME_START_NOTIFICATION = 5

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"

    private lateinit var eventDBHelper: EventDBHelper
    private var mNotificationManager: NotificationManagerCompat? = null

    private val CHANNEL_ID = "eventChannel"

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)

        startTimer()

        return Service.START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")

        mNotificationManager = NotificationManagerCompat.from(getApplicationContext())

        eventDBHelper = EventDBHelper(this)
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    val handler = Handler()

    fun startTimer() {
        timer = Timer()

        initializeTimerTask()

        timer!!.schedule(
                timerTask,
                900000,
                DEFAULT_TIME_START_NOTIFICATION.toLong() * 30000
        )
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask() {

        timerTask = object : TimerTask() {
            override fun run() {
                handler.post{
                    sendNotifications()
                }
            }
        }
    }

    fun sendNotifications() {

        val events = eventDBHelper.readAllEventsForToday()
        var notificationID = 0

        for (event in events) {

            mNotificationManager!!.notify(notificationID, createNotification(event))
            notificationID++

        }
    }

    fun createNotification(event: EventModel) : Notification
    {
        var intent = Intent(this, HomeActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val dismissIntent = Intent(this, NotificationIntentService::class.java)
        dismissIntent.action = ACTION_DISMISS

        dismissIntent.putExtra("event", event)

        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)
        val dismissAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_check,
                "Ok",
                dismissPendingIntent)
                .build()


        val reportShortIntent = Intent(this, NotificationIntentService::class.java)
        reportShortIntent.action = ACTION_REPORT_SHORT

        reportShortIntent.putExtra("event", event)

        val reportShortPendingIntent = PendingIntent.getService(this, 0, reportShortIntent, 0)

        val reportShortAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "Report : 5 minutes",
                reportShortPendingIntent)
                .build()

        val reportLongIntent = Intent(this, NotificationIntentService::class.java)
        reportLongIntent.action = ACTION_REPORT_LONG

        reportLongIntent.putExtra("event", event)

        val reportLongPendingIntentService = PendingIntent.getService(this, 0, reportLongIntent, 0)
        val reportlongAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "Report : 1 heure",
                reportLongPendingIntentService)
                .build()

        val notificationCompatBuilder = NotificationCompat.Builder(applicationContext)

        return notificationCompatBuilder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Evenement Aujourd'hui : " + event.name)
                .setContentText(event.description)
                .setChannelId(CHANNEL_ID)
                .addAction(dismissAction)
                .addAction(reportShortAction)
                .addAction(reportlongAction)
                .build()
    }
}