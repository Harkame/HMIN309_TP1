package fr.harkame.tp1

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import fr.tp1.harkame.tp1.R
import android.os.Vibrator
import android.widget.TextView
import android.hardware.SensorManager




class MainActivity : WearableActivity(), SensorEventListener {
    private val LOCATION_INTERVAL = 10000L
    private val LOCATION_DISTANCE = 10f

    private val lastX: Float = 0F
    var lastY: Float = 0F
    var lastZ: Float = 0F

    private var deltaXMax = 0F
    private var deltaYMax = 0F
    private var deltaZMax = 0F

    private var deltaX = 0F
    private var deltaY = 0F
    private var deltaZ = 0F

    private var vibrateThreshold = 0f

    var v: Vibrator? = null

    private lateinit var speedTextView : TextView

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speedTextView = findViewById(R.id.text_speed)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            vibrateThreshold = accelerometer.maximumRange / 2
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setAmbientEnabled()
    }

    override fun onSensorChanged(event: SensorEvent?) {

        //displayCleanValues()

        displayCurrentValues()

        displayMaxValues()

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event!!.values[0])
        deltaY = Math.abs(lastY - event.values[1])
        deltaZ = Math.abs(lastZ - event.values[2])


    }

    fun displayCurrentValues() {
        //currentX.setText(java.lang.Float.toString(deltaX))
        //currentY.setText(java.lang.Float.toString(deltaY))
        //currentZ.setText(java.lang.Float.toString(deltaZ))
    }

    // display the max x,y,z accelerometer values
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
}
