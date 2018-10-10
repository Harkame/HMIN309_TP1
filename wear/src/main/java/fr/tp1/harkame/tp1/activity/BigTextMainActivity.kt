package fr.tp1.harkame.tp1.activity

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import fr.tp1.harkame.tp1.R

class BigTextMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_text_main)

        // Cancel Notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(MainActivity.NOTIFICATION_ID)
    }
}