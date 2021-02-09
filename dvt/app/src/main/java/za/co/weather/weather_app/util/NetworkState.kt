package za.co.weather.weather_app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkState  {

    companion object {

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
}
