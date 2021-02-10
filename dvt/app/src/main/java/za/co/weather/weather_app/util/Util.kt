package za.co.weather.weather_app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.model.DailyTemperatureData
import za.co.weather.weather_app.retrofit.WeatherAPIEndpoint

class Util {

    companion object {

        var cityName = ""

        fun isWiFiConnected(context: Context): Boolean {
            var connected = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val networkInfo = connectivityManager.activeNetwork ?: return false

                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(networkInfo) ?: return false

                if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    connected = true
                }
            }

            return connected
        }

        fun isMobileDataConnected(context: Context): Boolean {
            var connected = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkInfo = connectivityManager.activeNetwork ?: return false

                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(networkInfo) ?: return false

                if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    connected = true
                }
            }
            return connected
        }

        fun serializeToJSONObject(linkedTreeMap: LinkedTreeMap<Any, Any>?): JSONObject? {
            val gson = Gson()
            val json: JsonObject = gson.toJsonTree(linkedTreeMap).asJsonObject
            return JSONObject(json.toString())
        }

        suspend fun makeApiCall(
            componentActivity: ComponentActivity,
            city: String
        ): Pair<CurrentTemperatureData?, ArrayList<DailyTemperatureData>?> {

            var current: CurrentTemperatureData? = null
            var forecast = arrayListOf<DailyTemperatureData>()

            when (isMobileDataConnected(componentActivity) || isWiFiConnected(componentActivity)) {

                true -> {

                    when (city.isBlank()) {
                        false -> {
                            componentActivity.lifecycleScope.launch(Dispatchers.IO) {

                                val resCurrent = WeatherAPIEndpoint.getWeatherCityCALL(city)
                                val resForecast =
                                    WeatherAPIEndpoint.getWeatherCityForecastCALL(city)

                                if (resCurrent != null) {

                                    val jsonObject =
                                        serializeToJSONObject(resCurrent as LinkedTreeMap<Any, Any>)
                                    if (jsonObject != null) {
                                        current =
                                            CurrentTemperatureData(
                                                jsonObject.getJSONObject("coord"),
                                                jsonObject.getJSONArray("weather"),
                                                jsonObject.getString("base"),
                                                jsonObject.getJSONObject("main"),
                                                jsonObject.getDouble("visibility"),
                                                jsonObject.getJSONObject("wind"),
                                                jsonObject.getJSONObject("clouds"),
                                                "${jsonObject.getDouble("dt")}",
                                                jsonObject.getJSONObject("sys"),
                                                jsonObject.getLong("timezone"),
                                                "${jsonObject.getLong("id")}",
                                                jsonObject.getString("name"),
                                                "${jsonObject.getInt("cod")}"
                                            )
                                    }
                                }

                                if (resForecast != null) {

                                    val jsonObject =
                                        serializeToJSONObject(resForecast as LinkedTreeMap<Any, Any>)
                                    forecast = arrayListOf()
                                    if (jsonObject != null) {
                                        val list = jsonObject.getJSONArray("list")
                                        for (i in 0 until list.length()) {

                                            val item = list.getJSONObject(i)

                                            if (item.getString("dt_txt").contains("12:00")) {

                                                val entry =
                                                    DailyTemperatureData(
                                                        "${item.getDouble("dt")}",
                                                        item.getJSONObject("main"),
                                                        item.getJSONArray("weather"),
                                                        item.getJSONObject("clouds"),
                                                        item.getJSONObject("wind"),
                                                        item.getDouble("visibility"),
                                                        item.getDouble("pop"),
                                                        if (item.has("rain")) item.getJSONObject("rain") else JSONObject(),
                                                        item.getJSONObject("sys"),
                                                        item.getString("dt_txt")
                                                    )
                                                forecast.add(entry)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        true -> {
                        }
                    }
                }
                false -> {
                    Toast.makeText(
                        componentActivity,
                        "No stable internet connection to update",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            delay(2000)
            return Pair(current, forecast)
        }

        suspend fun makeApiCall(
            componentActivity: ComponentActivity,
            lat: Double?,
            lon: Double?
        ): Pair<CurrentTemperatureData?, ArrayList<DailyTemperatureData>?> {

            var current: CurrentTemperatureData? = null
            var forecast = arrayListOf<DailyTemperatureData>()

            when (isMobileDataConnected(componentActivity) || isWiFiConnected(componentActivity)) {

                true -> {

                    when (lat != null && lon != null) {
                        true -> {
                            componentActivity.lifecycleScope.launch(Dispatchers.IO) {

                                val resCurrent = WeatherAPIEndpoint.getWeatherCurrentCALL(lat, lon)
                                val resForecast =
                                    WeatherAPIEndpoint.getWeatherForecastCALL(lat, lon)

                                if (resCurrent != null) {

                                    val jsonObject =
                                        serializeToJSONObject(resCurrent as LinkedTreeMap<Any, Any>)
                                    if (jsonObject != null) {
                                        current =
                                            CurrentTemperatureData(
                                                jsonObject.getJSONObject("coord"),
                                                jsonObject.getJSONArray("weather"),
                                                jsonObject.getString("base"),
                                                jsonObject.getJSONObject("main"),
                                                jsonObject.getDouble("visibility"),
                                                jsonObject.getJSONObject("wind"),
                                                jsonObject.getJSONObject("clouds"),
                                                "${jsonObject.getDouble("dt")}",
                                                jsonObject.getJSONObject("sys"),
                                                jsonObject.getLong("timezone"),
                                                "${jsonObject.getLong("id")}",
                                                jsonObject.getString("name"),
                                                "${jsonObject.getInt("cod")}"
                                            )
                                    }
                                }

                                if (resForecast != null) {

                                    val jsonObject =
                                        serializeToJSONObject(resForecast as LinkedTreeMap<Any, Any>)
                                    forecast = arrayListOf()
                                    if (jsonObject != null) {
                                        val list = jsonObject.getJSONArray("list")
                                        for (i in 0 until list.length()) {

                                            val item = list.getJSONObject(i)

                                            if (item.getString("dt_txt").contains("12:00")) {

                                                val entry =
                                                    DailyTemperatureData(
                                                        "${item.getDouble("dt")}",
                                                        item.getJSONObject("main"),
                                                        item.getJSONArray("weather"),
                                                        item.getJSONObject("clouds"),
                                                        item.getJSONObject("wind"),
                                                        item.getDouble("visibility"),
                                                        item.getDouble("pop"),
                                                        if (item.has("rain")) item.getJSONObject("rain") else JSONObject(),
                                                        item.getJSONObject("sys"),
                                                        item.getString("dt_txt")
                                                    )
                                                forecast.add(entry)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        false -> {
                        }
                    }
                }
                false -> {
                    Toast.makeText(
                        componentActivity,
                        "No stable internet connection to update",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            delay(2000)
            return Pair(current, forecast)
        }
    }
}
