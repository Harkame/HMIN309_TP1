package fr.tp1.harkame.tp1.activity

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import fr.tp1.harkame.tp1.R

import java.util.concurrent.TimeUnit

class BigTextIntentService : IntentService("BigTextIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent(): " + intent!!)

        if (intent != null) {
            val action = intent.action
            if (ACTION_DISMISS == action) {
                handleActionDismiss()
            } else if (ACTION_SNOOZE == action) {
                handleActionSnooze()
            }
        }
    }

    private fun handleActionDismiss() {
        Log.d(TAG, "handleActionDismiss()")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        //notificationManagerCompat.cancel(MainActivity.NOTIFICATION_ID)
    }

    private fun handleActionSnooze() {
        Log.d(TAG, "handleActionSnooze()")
        var notificationCompatBuilder = GlobalNotificationBuilder.notificationCompatBuilderInstance

        // Recreate builder from persistent state if app process is killed
        if (notificationCompatBuilder == null) {
            // Note: New builder set globally in the method
            notificationCompatBuilder = recreateBuilderWithBigTextStyle()
        }

        val notification: Notification?
        notification = notificationCompatBuilder!!.build()


        if (notification != null) {
            val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

            //notificationManagerCompat.cancel(MainActivity.NOTIFICATION_ID)

            try {
                Thread.sleep(SNOOZE_TIME)
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            //notificationManagerCompat.notify(MainActivity.NOTIFICATION_ID, notification!!)
        }

    }

    private fun recreateBuilderWithBigTextStyle(): NotificationCompat.Builder {
        val notificationChannelId = "channel_social_1"

        val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText("Text")
                .setBigContentTitle("Title")
                .setSummaryText("Summary text")

        val mainIntent = Intent(this, BigTextMainActivity::class.java)

        val mainPendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val snoozeIntent = Intent(this, BigTextIntentService::class.java)
        snoozeIntent.action = ACTION_SNOOZE

        val snoozePendingIntent = PendingIntent.getService(this, 0, snoozeIntent, 0)
        val snoozeAction = NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Snooze",
                snoozePendingIntent)
                .build()

        // Dismiss Action
        val dismissIntent = Intent(this, BigTextIntentService::class.java)
        dismissIntent.action = ACTION_DISMISS

        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)
        val dismissAction = NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Dismiss",
                dismissPendingIntent)
                .build()

        val notificationCompatBuilder = NotificationCompat.Builder(
                applicationContext, notificationChannelId)

        GlobalNotificationBuilder.notificationCompatBuilderInstance = notificationCompatBuilder

        notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle("Title")
                .setContentText("Text")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(snoozeAction)
                .addAction(dismissAction)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            val mainAction = NotificationCompat.Action.Builder(
                    R.mipmap.ic_launcher,
                    "Open",
                    mainPendingIntent)
                    .build()

            notificationCompatBuilder.addAction(mainAction)

        } else {
            notificationCompatBuilder.setContentIntent(mainPendingIntent)
        }

        return notificationCompatBuilder
    }

    companion object {

        private val TAG = "BigTextService"

        val ACTION_DISMISS = "com.example.android.wearable.wear.wearnotifications.handlers.action.DISMISS"
        val ACTION_SNOOZE = "com.example.android.wearable.wear.wearnotifications.handlers.action.SNOOZE"

        private val SNOOZE_TIME = TimeUnit.SECONDS.toMillis(5)
    }
}
