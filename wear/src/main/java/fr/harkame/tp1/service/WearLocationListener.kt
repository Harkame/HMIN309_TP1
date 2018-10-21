package fr.harkame.tp1.service
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class WearLocationListener(private val textView: TextView) : LocationListener {
    val TAG = "WearLocationListener"

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged ".plus(location.speed))
                textView.text = location.speed.toString()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d(TAG, "onStatusChanged")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(TAG, "onProviderEnabled")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(TAG, "onProviderDisabled")
    }
}