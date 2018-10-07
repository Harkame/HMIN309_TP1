package fr.tp1.harkame.tp1.activity

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import fr.tp1.harkame.tp1.R

class MainActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()
    }
}
