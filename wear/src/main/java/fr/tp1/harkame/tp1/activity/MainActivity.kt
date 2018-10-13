package fr.tp1.harkame.tp1.activity

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.wear.ambient.AmbientMode
import android.util.Log
import android.widget.FrameLayout
import fr.tp1.harkame.tp1.R


class MainActivity : Activity(), AmbientMode.AmbientCallbackProvider {

    private var mNotificationManagerCompat: NotificationManagerCompat? = null

    private var mMainFrameLayout: FrameLayout? = null
    private var mRecyclerView: RecyclerView? = null
    private var mCustomRecyclerAdapter: CustomRecyclerAdapter? = null

    private var mAmbientController: AmbientMode.AmbientController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        setContentView(R.layout.activity_main)

        // Enables Ambient mode.
        mAmbientController = AmbientMode.attachAmbientSupport(this)

        mNotificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        mMainFrameLayout = findViewById(R.id.mainFrameLayout)
        mRecyclerView = findViewById(R.id.recycler_view)

        // Aligns the first and last items on the list vertically centered on the screen.
        //mRecyclerView!!.setEdgeItemsCenteringEnabled(true)

        // Customizes scrolling so items farther away form center are smaller.
        val scalingScrollLayoutCallback = ScalingScrollLayoutCallback()
        mRecyclerView!!.setLayoutManager(
                LinearLayoutManager(this))

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mRecyclerView!!.setHasFixedSize(true)

        // Specifies an adapter (see also next example).
        mCustomRecyclerAdapter = CustomRecyclerAdapter(
                NOTIFICATION_STYLES,
                // Controller passes selected data from the Adapter out to this Activity to trigger
                // updates in the UI/Notifications.
                Controller(this))

        mRecyclerView!!.setAdapter(mCustomRecyclerAdapter)
    }

    // Called by RecyclerView when an item is selected (check onCreate() for
    // initialization).
    fun itemSelected(data: String) {

        Log.d(TAG, "itemSelected()")

        val areNotificationsEnabled = mNotificationManagerCompat!!.areNotificationsEnabled()

        // If notifications are disabled, allow user to enable.
        if (!areNotificationsEnabled) {
            // Because the user took an action to create a notification, we create a prompt to let
            // the user re-enable notifications for this application again.
            /*
            val snackbar = Snackbar
                    .make(
                            mMainFrameLayout!!,
                            "", // Not enough space for both text and action text.
                            Snackbar.LENGTH_LONG)
                    .setAction("Enable Notifications", View.OnClickListener {
                        // Links to this app's notification settings.
                        openNotificationSettingsForApp()
                    })
            snackbar.show()
            */
            return
        }

        when (data) {
            BIG_TEXT_STYLE -> generateBigTextStyleNotification()
        }// continue below.
    }

    private fun generateBigTextStyleNotification() {

        Log.d(TAG, "generateBigTextStyleNotification()")

        val notificationChannelId = "eventChannel"

        val bigTextStyle = NotificationCompat.BigTextStyle()
                // Overrides ContentText in the big form of the template.
                .bigText("Test")
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle("Title")
                // Summary line after the detail section in the big form of the template
                // Note: To improve readability, don't overload the user with info. If Summary Text
                // doesn't add critical information, you should skip it.
                .setSummaryText("Summer text")


        // 3. Set up main Intent for notification.
        val mainIntent = Intent(this, BigTextMainActivity::class.java)

        val mainPendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )


        // 4. Create additional Actions (Intents) for the Notification.

        // In our case, we create two additional actions: a Snooze action and a Dismiss action.

        // Snooze Action.
        val snoozeIntent = Intent(this, BigTextIntentService::class.java)
        snoozeIntent.action = BigTextIntentService.ACTION_SNOOZE

        val snoozePendingIntent = PendingIntent.getService(this, 0, snoozeIntent, 0)
        val snoozeAction = NotificationCompat.Action.Builder(
                R.drawable.ic_launcher,
                "Snooze",
                snoozePendingIntent)
                .build()

        // Dismiss Action.
        val dismissIntent = Intent(this, BigTextIntentService::class.java)
        dismissIntent.action = BigTextIntentService.ACTION_DISMISS

        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)
        val dismissAction = NotificationCompat.Action.Builder(
                R.drawable.ic_launcher,
                "Dismiss",
                dismissPendingIntent)
                .build()

        val notificationCompatBuilder = NotificationCompat.Builder(
                applicationContext, notificationChannelId)

        GlobalNotificationBuilder.notificationCompatBuilderInstance = notificationCompatBuilder

        notificationCompatBuilder
                // BIG_TEXT_STYLE sets title and content.
                .setStyle(bigTextStyle)
                .setContentTitle("Title")
                .setContentText("content text")
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_launcher))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))

                .setCategory(Notification.CATEGORY_REMINDER)


                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Adds additional actions specified above.
                .addAction(snoozeAction)
                .addAction(dismissAction)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // Enables launching app in Wear 2.0 while keeping the old Notification Style behavior.
            val mainAction = NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher,
                    "Open",
                    mainPendingIntent)
                    .build()

            notificationCompatBuilder.addAction(mainAction)

        } else {
            // Wear 1.+ still functions the same, so we set the main content intent.
            notificationCompatBuilder.setContentIntent(mainPendingIntent)
        }


        val notification = notificationCompatBuilder.build()

        mNotificationManagerCompat!!.notify(NOTIFICATION_ID, notification)

        // Close app to demonstrate notification in steam.
        finish()
    }

    private fun openNotificationSettingsForApp() {
        // Links to this app's notification settings
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", packageName)
        intent.putExtra("app_uid", applicationInfo.uid)
        startActivity(intent)
    }

    override fun getAmbientCallback(): AmbientMode.AmbientCallback {
        return MyAmbientCallback()
    }

    private inner class MyAmbientCallback : AmbientMode.AmbientCallback() {
        /** Prepares the UI for ambient mode.  */
        override fun onEnterAmbient(ambientDetails: Bundle?) {
            super.onEnterAmbient(ambientDetails)

            Log.d(TAG, "onEnterAmbient() " + ambientDetails!!)
        }

        /** Restores the UI to active (non-ambient) mode.  */
        override fun onExitAmbient() {
            super.onExitAmbient()

            Log.d(TAG, "onExitAmbient()")
        }
    }

    companion object {

        private val TAG = "MainActivity"

        val NOTIFICATION_ID = 888

        private val BIG_TEXT_STYLE = "BIG_TEXT_STYLE"
        private val BIG_PICTURE_STYLE = "BIG_PICTURE_STYLE"
        private val INBOX_STYLE = "INBOX_STYLE"
        private val MESSAGING_STYLE = "MESSAGING_STYLE"

        private val NOTIFICATION_STYLES = arrayOf(BIG_TEXT_STYLE)
    }

}
