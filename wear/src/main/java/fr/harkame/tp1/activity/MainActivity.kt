package fr.harkame.tp1.activity

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.TextView
import android.hardware.SensorManager
import android.util.Log
import android.widget.Button
import com.github.jrejaud.wear_socket.WearSocket
import fr.harkame.tp1.R
import fr.harkame.tp1.service.MessageListenerService
import com.github.jrejaud.wear_socket.WearSocket.onErrorListener




class MainActivity : WearableActivity(), SensorEventListener, WearSocket.MessageListener {

    companion object {
        val TAG = "MainActivity"
        val LOCATION_INTERVAL = 10000L
        val LOCATION_DISTANCE = 10f
    }

    private val lastX: Float = 0F
    var lastY: Float = 0F
    var lastZ: Float = 0F

    private var deltaXMax = 0F
    private var deltaYMax = 0F
    private var deltaZMax = 0F

    private var deltaX = 0F
    private var deltaY = 0F
    private var deltaZ = 0F

    private lateinit var speedTextView : TextView
    private lateinit var startbutton : Button

    private var started : Boolean = false

    private lateinit var wearSocket : WearSocket

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startbutton = findViewById(R.id.button_start)

        speedTextView = findViewById(R.id.text_speed)

        startbutton.setOnClickListener{

            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            if(started == true)
            {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    sensorManager.unregisterListener(this)

                    startbutton.text = getString(R.string.start)

                    speedTextView.text = "0.0"

                    started = false
                }
            }
            else {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

                    startbutton.text = getString(R.string.stop)

                    started = true
                }
            }
        }

        wearSocket = WearSocket.getInstance();

        wearSocket.setupAndConnect(this, "voice_transcription") {
            //Throws an error here if there is a problem connecting to the other device.
        }

        wearSocket.startMessageListener(this,"/start-activity")

        startService(Intent(this, MessageListenerService::class.java))

        setAmbientEnabled()
    }

    override fun onSensorChanged(event: SensorEvent?) {

        displayCurrentValues()

        displayMaxValues()

        deltaX = Math.abs(lastX - event!!.values[0])
        deltaY = Math.abs(lastY - event.values[1])
        deltaZ = Math.abs(lastZ - event.values[2])


    }

    fun displayCurrentValues() {

    }

    fun displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX
            speedTextView.setText(java.lang.Float.toString(deltaXMax))
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY
            speedTextView.setText(java.lang.Float.toString(deltaYMax))
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ
            speedTextView.setText(java.lang.Float.toString(deltaZMax))
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun messageReceived(path: String, message: String) {
        Log.d(TAG, path.plus(" [NOUVEAU MESSAGE] ").plus(message))
    }
}
