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
import android.os.Handler
import android.util.Log
import android.widget.Button
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import fr.harkame.tp1.R
import fr.harkame.tp1.service.MessageListenerService
import java.util.*

class MainActivity : WearableActivity(), SensorEventListener
{
    companion object {
        val TAG = "MainActivity"
        val LOCATION_INTERVAL = 10000L
        val LOCATION_DISTANCE = 10f

        private const val WEAR_DATA_PATH = "/wear-data"
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

    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var handler : Handler

    private val DEFAULT_TIME_START_NOTIFICATION = 10L

    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var mNode: Node

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

                    stoptimertask()

                    started = false
                }
            }
            else {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

                    startbutton.text = getString(R.string.stop)

                    startTimer()

                    started = true
                }
            }
        }

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
            speedTextView.text = java.lang.Float.toString(deltaXMax)
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY
            speedTextView.text = java.lang.Float.toString(deltaYMax)
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ
            speedTextView.text = java.lang.Float.toString(deltaZMax)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    fun startTimer() {
        timer = Timer()

        initializeTimerTask()

        timer.schedule(
                timerTask,
                DEFAULT_TIME_START_NOTIFICATION,
                DEFAULT_TIME_START_NOTIFICATION * 300
        )
    }

    fun stoptimertask()
    {
        timer.cancel()
    }

    fun initializeTimerTask() {

        handler = Handler()
        timerTask = object : TimerTask()
        {
            override fun run()
            {
                handler.post{
                    resolveNode(speedTextView.text.toString())
                }
            }
        }
    }

    private fun resolveNode(data: String) {
        Log.d(TAG, "resolveNode")

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build()

        mGoogleApiClient.connect()

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback { connectedNodes ->
                    for (connectedNode in connectedNodes.nodes) {
                        mNode = connectedNode
                        sendMessage(WEAR_DATA_PATH, data)
                    }
                }
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
                            Log.d(TAG, "Message sended : " + message)
                        else
                            Log.e(TAG, "Message not sended")
                    }
                })
    }
}
