package za.co.weather.weather_app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.layout_weather_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.model.ForecastTemperatureData
import za.co.weather.weather_app.retrofit.WeatherAPIEndpoint
import za.co.weather.weather_app.views.ForecastAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class Util {

    companion object {

        var cityName = ""
        private val todayFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        private val lastUpdatedFormat = SimpleDateFormat("MMMM, dd yyyy HH:mm:ss", Locale.ENGLISH)

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
        ): Pair<CurrentTemperatureData?, ArrayList<ForecastTemperatureData>?> {

            var current: CurrentTemperatureData? = null
            var forecast = arrayListOf<ForecastTemperatureData>()

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
                                                    ForecastTemperatureData(
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
                        (componentActivity as AppCompatActivity).getString(R.string.internet_not_stable),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            delay(2000)
            return Pair(current, forecast)
        }

        suspend fun makeApiCall(componentActivity: ComponentActivity,
            lat: Double?,
            lon: Double?
        ): Pair<CurrentTemperatureData?, ArrayList<ForecastTemperatureData>?> {

            var current: CurrentTemperatureData? = null
            var forecast = arrayListOf<ForecastTemperatureData>()

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
                                                    ForecastTemperatureData(
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
                        (componentActivity as AppCompatActivity).getString(R.string.internet_not_stable),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            delay(2000)
            return Pair(current, forecast)
        }

        fun convertDateToDay(date: String): String? {

            val calendar = Calendar.getInstance()
            calendar.time = todayFormat.parse(date)

            return getDayString(calendar.get(Calendar.DAY_OF_WEEK))
        }


        private fun getDayString(day: Int): String? {
            when (day) {
                1 -> { return "Monday" }
                2 -> { return "Tuesday" }
                3 -> { return "Wednesday" }
                4 -> { return "Thursday" }
                5 -> { return "Friday" }
                6 -> { return "Saturday" }
                7 -> { return "Sunday" }
            }
            return ""
        }

        fun convertLongToTime(time: Long?): String {
            var result = ""
            if (time != null) {
               result = when (time > 0) {
                    true -> {
                        val date = Date(time)
                        lastUpdatedFormat.format(date)
                    }
                    else -> {
                        val date = Date(System.currentTimeMillis())
                        lastUpdatedFormat.format(date)
                    }
                }
            }
            return result
        }


        fun updateScreen(context: AppCompatActivity, currentTemperatureData: CurrentTemperatureData?, forecast: ArrayList<ForecastTemperatureData>?) {

            if (currentTemperatureData == null || context.temp_number_main == null) {
                return
            }

            println(currentTemperatureData)
            context.temp_number_main.text =
                "${currentTemperatureData.main.getDouble("temp").roundToInt()}\u00B0"
            context.temp_condition_main.text = currentTemperatureData.weather.getJSONObject(0)
                .getString("main").toUpperCase(Locale.ENGLISH)

            context.temp_min_text.text =
                "${currentTemperatureData.main.getDouble("temp_min").roundToInt()}\u00B0\nmin"
            context.temp_curr_text.text =
                "${currentTemperatureData.main.getDouble("temp").roundToInt()}\u00B0\nCurrent"
            context.temp_max_text.text =
                "${currentTemperatureData.main.getDouble("temp_max").roundToInt()}\u00B0\nmax"

            context.curr_location.text =
                "${currentTemperatureData.name}, ${currentTemperatureData.sys.getString("country")}"
            context.curr_humidity.text =
                "Humidity\n${currentTemperatureData.main.getDouble("humidity").roundToInt()}%"
            context.curr_pressure.text =
                "Pressure\n${currentTemperatureData.main.getDouble("pressure").roundToInt()}hPa"

            context.curr_feel.text =
                "Feels like\n${currentTemperatureData.main.getDouble("feels_like").roundToInt()}\u00B0"
            context.curr_visibility.text = "Visibility\n${(currentTemperatureData.visibility / 1000)}km"

            context.curr_last_updated.text = "Last updated\n${(convertLongToTime(System.currentTimeMillis()))}"


            when (currentTemperatureData.weather.getJSONObject(0).getString("main")
                .contains("cloud", true)) {

                true -> {
                    context.background_main.background =
                        ActivityCompat.getDrawable(context, R.drawable.forest_cloudy)
                    context.main_layout.background =
                        ActivityCompat.getDrawable(context, R.drawable.main_background_cloud)
                }
                false -> {
                    when (currentTemperatureData.weather.getJSONObject(0).getString("main")
                        .contains("clear", true)) {

                        true -> {
                            context.background_main.background =
                                ActivityCompat.getDrawable(context, R.drawable.forest_sunny)
                            context.main_layout.background = ActivityCompat.getDrawable(
                                context,
                                R.drawable.main_background_clear
                            )
                        }
                        false -> {
                            when (currentTemperatureData.weather.getJSONObject(0).getString("main")
                                .contains("rain", true)) {
                                true -> {
                                    context.background_main.background = ActivityCompat.getDrawable(
                                        context,
                                        R.drawable.forest_rainy
                                    )
                                    context.main_layout.background = ActivityCompat.getDrawable(
                                        context,
                                        R.drawable.main_background_rain
                                    )
                                }
                                false -> {
                                }
                            }
                        }
                    }
                }
            }

            val forecastAdapter = ForecastAdapter(context, forecast)
            context.recycler_view_daily_forecast.visibility = ViewGroup.VISIBLE
            context.recycler_view_daily_forecast.adapter = forecastAdapter
            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            context.recycler_view_daily_forecast.layoutManager = layoutManager
        }



    }
}
