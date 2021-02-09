package za.co.weather.weather_app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CustomLocationListener : LocationListener{
    private var mContext: Context? = null

    // flag for GPS status
    private var isGPSEnabled = false

    // flag for network status
    private var isNetworkEnabled = false

    // location
    var currentLocation : Location? = null

    // latitude
    private var latitude = 0.0

    // longitude
    private var longitude = 0.0

    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1F // 10 meters

    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    constructor(context: Context) {
        this.mContext = context
    }


    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            currentLocation = p0

            println("lat: ${currentLocation?.latitude} \tlon: lat: ${currentLocation?.longitude}")

//            latitude = p0.latitude
//            longitude = p0.longitude
        }

    }


    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) { }


    fun getCurrentLoc() {

        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this@CustomLocationListener)
        }






    }





    fun isGPSEnabled() :Boolean {

        return (ContextCompat.checkSelfPermission(
            mContext!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    mContext!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)

    }


    fun isWiFiConnected(context: Context): Boolean {
        var connected = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val networkInfo = connectivityManager.activeNetwork ?: return false

                val activeNetwork = connectivityManager.getNetworkCapabilities(networkInfo) ?: return false

                if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    connected = true
                }
        }

        return connected
    }

    fun isMobileDataConnected(context: Context): Boolean {
        var connected = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkInfo = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(networkInfo) ?: return false

            if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                connected = true
            }
        }

        return connected
    }



}





















//fun getLocation(): Location? {
//    try {
//        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        // getting GPS status
//        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        Log.v("isGPSEnabled", "=$isGPSEnabled")
//
//        // getting network status
//        isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        Log.v("isNetworkEnabled", "=$isNetworkEnabled")
//
//        if (isGPSEnabled && isNetworkEnabled) { // network provider is enabled
//
////                canGetLocation = true
//
//            if (isNetworkEnabled) {
//                currentLocation = null
//                //
//
//                /**
//                 * check gps avail here.
//                 */
//                if ((ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                            && ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
//
//
//                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),this)
//                    Log.d("Network", "Network")
//
//                    if (locationManager != null) {
//                        currentLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//
//                        if (currentLocation != null) {
//                            latitude = currentLocation!!.latitude
//                            longitude = currentLocation!!.longitude
//                        }
//                    }
//                }
//            }
//
//
//
//            // if GPS Enabled get lat/long using GPS Services
//            if (isGPSEnabled) {
//                currentLocation = null
//                if (currentLocation == null) {
//
//
//                    /**
//                     * check gps avail here
//                     */
//
//                    if((ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                                && ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
//
//                        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
//
//
//                        Log.d("GPS Enabled", "GPS Enabled")
//                        if (locationManager != null) {
//                            currentLocation = locationManager!!
//                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                            if (currentLocation != null) {
//                                latitude = currentLocation!!.latitude
//                                longitude = currentLocation!!.longitude
//                            }
//                        }
//                    }}
//            }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    return currentLocation
//}
