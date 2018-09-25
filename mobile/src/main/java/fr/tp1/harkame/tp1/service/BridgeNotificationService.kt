package fr.tp1.harkame.tp1.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import fr.tp1.harkame.tp1.R
import fr.tp1.harkame.tp1.activity.home.HomeActivity
import fr.tp1.harkame.tp1.db.helper.EventDBHelper
import java.util.*

/**
 * Created by Antho on 25/09/2018.
 */

// notification for handled device and wearable. when a notification came on the phone it pop on the wearable too.
class BridgeNotificationService : Service() {

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"
    var Your_X_SECS = 10

    private lateinit var eventDBHelper: EventDBHelper
    private var mNotificationManager: NotificationManager? = null

    private val CHANNEL_ID = "BridgeNotificationChannel"

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
        mNotificationManager = createNotificationChannel()
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    //we are going to use a handler to be able to run in our TimerTask
    val handler = Handler()

    fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, after the first 10sec the TimerTask will run every 5 minutes
        timer!!.schedule(
                timerTask,
                10000,
                Your_X_SECS.toLong() * 30000
        ) //
        //timer.schedule(timerTask, 5000,1000); //
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

                //use a handler to run a toast that shows the current timestamp
                handler.post(Runnable {
                    //TODO CALL NOTIFICATION FUNC
                    sendNotification()
                })
            }
        }
    }

    //si ça ne fonctionne pas c'est peut être qu'il faut ajouter le createChannel...
    fun sendNotification() {

        val events = eventDBHelper.readAllEventsForToday()
        var notificationID = 0

        var intent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        for (event in events) {

            // Build the shape of the notification
            val mNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Evenement Aujourd'hui : " + event.name)
                    .setContentText(event.description)
                    .setChannelId(CHANNEL_ID)
                    .build()

            notificationID++
            // Gets an instance of the NotificationManager service//
            mNotificationManager!!.notify(notificationID, mNotification)
        }
    }

    fun createNotificationChannel(): NotificationManager? {
        var mNotificationManager: NotificationManager? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var name = getString(R.string.channel_name)
            var description = getString(R.string.channel_description)
            var importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.setDescription(description)

            // Gets an instance of the NotificationManager service//
            mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.createNotificationChannel(channel)

            return mNotificationManager
        }
        return mNotificationManager
    }
}