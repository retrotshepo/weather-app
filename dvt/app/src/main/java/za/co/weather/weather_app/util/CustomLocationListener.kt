package za.co.weather.weather_app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import za.co.weather.weather_app.util.SharedPreferencesHandler.Companion.LATITUDE
import za.co.weather.weather_app.util.SharedPreferencesHandler.Companion.LONGITUDE
import za.co.weather.weather_app.util.SharedPreferencesHandler.Companion.saveValue
import za.co.weather.weather_app.util.Util.Companion.isMobileDataConnected
import za.co.weather.weather_app.util.Util.Companion.isWiFiConnected

class CustomLocationListener(var context: Context) : LocationListener {

    private var currentLocation : Location? = null
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_FOR_UPDATE: Float = 1F
    private val MIN_TIME_INTERVAL: Long = 1

    private var locationManager: LocationManager? = null

    init {
        getLastKnownLocationGPS(this.context)
        getLastKnownLocationNetwork(this.context)
    }

    override fun onLocationChanged(p0: Location?) {

        if (p0 != null) {
            currentLocation = p0
            latitude = p0.latitude
            longitude = p0.longitude

            when (latitude != 0.0 && longitude != 0.0) {
                true -> {
                    saveValue(LATITUDE, latitude?.toFloat())
                    saveValue(LONGITUDE, longitude?.toFloat())
                }
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { }

    override fun onProviderEnabled(p0: String?) { }

    override fun onProviderDisabled(p0: String?) { }

    fun getLatitude(): Double? {
        return latitude
    }

    fun getLongitude(): Double? {
        return longitude
    }

    private fun getLastKnownLocationNetwork(context: Context) {

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isInternetAvailable = isMobileDataConnected(context) || isWiFiConnected(context)

        if (isInternetAvailable) { // network provider is enabled
            currentLocation = null
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE_FOR_UPDATE, this)

                currentLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (currentLocation != null) {
                    latitude = currentLocation?.latitude
                    longitude = currentLocation?.longitude
                }
            }
        }
    }

    private fun getLastKnownLocationGPS(context: Context) {

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSAvailable = if(locationManager != null) locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) else false

        if (isGPSAvailable!!) {
            currentLocation = null
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)) {

                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE_FOR_UPDATE, this)

                currentLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (currentLocation != null) {
                    latitude = currentLocation?.latitude
                    longitude = currentLocation?.longitude
                }
            }
        }
    }

    fun stopGPS() {
        println("location manager stopped")
        if (locationManager != null) {
            locationManager?.removeUpdates(this)
        }
    }
}
