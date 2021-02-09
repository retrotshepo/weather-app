package za.co.weather.weather_app.views

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.layout_weather_screen.*
import kotlinx.android.synthetic.main.navigation_view_header.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import za.co.weather.weather_app.R
import za.co.weather.weather_app.util.CustomLocationListener
import za.co.weather.weather_app.util.WeatherAPIEndpoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherScreen : Fragment() {

    private var current: CurrentTemperatureData? = null
    var forecast = arrayListOf<DailyTemperatureData>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_weather_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("WeatherScreen onViewCreated")




        temp_day_container.setOnClickListener {
            println("tuts een twe")
//            apitest()

            val gps = CustomLocationListener(requireContext())
            gps.getCurrentLoc()

            println("lat: ${gps.currentLocation?.latitude} \tlon: lon: ${gps.currentLocation?.longitude}")

        }
    }







    fun apitest() {
        println("hello world!")

        this@WeatherScreen.lifecycleScope.launch (Dispatchers.IO) {

//                [-26.5603, 27.8625] orange farm
//            -26.1846304,28.0014622 ACK
//            -33.9152208,18.3758763 CPT
//            -30.7158878,30.3887406 NPS
            val resCurrent = WeatherAPIEndpoint.getWeatherCurrentCALL(-26.5603, 27.8625)
            val resForecast = WeatherAPIEndpoint.getWeatherForecastCALL(-26.5603, 27.8625)

            if (resCurrent != null) {

                val res = serializeToJSONObject(resCurrent as LinkedTreeMap<Any, Any>)

                if (res != null) {
                    current = CurrentTemperatureData(
                        res.getJSONObject("coord"), res.getJSONArray("weather"),
                        res.getString("base"), res.getJSONObject("main"),
                        res.getDouble("visibility"), res.getJSONObject("wind"),
                        res.getJSONObject("clouds"), "${res.getDouble("dt")}",
                        res.getJSONObject("sys"), res.getLong("timezone"),
                        "${res.getLong("id")}", res.getString("name"),
                        "${res.getInt("cod")}"
                    )

                }


//                println(" sunrise ${convertLongToTime(current?.sys?.getLong("sunrise"))}")
//                println(" sunset ${convertLongToTime(current?.sys?.getLong("sunset"))}")
                println(res)

            }

            if (resForecast != null) {

                val res = serializeToJSONObject(resForecast as LinkedTreeMap<Any, Any>)
forecast = arrayListOf()
                if (res != null) {
                    val list = res.getJSONArray("list")
                    for(i in 0 until list.length()) {

                        val item = list.getJSONObject(i)

                        if(item.getString("dt_txt").contains("12:00")) {

                            val entry = DailyTemperatureData(
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

                println(res)
            }
        }
        Handler().postDelayed({
            updateScreen(current)
        }, 3000)
    }

    private fun updateScreen(currentTemperatureData: CurrentTemperatureData?) {

        if(currentTemperatureData == null) {
            return
        }

        temp_number_main.text = "${currentTemperatureData.main.getDouble("temp").roundToInt()}\u00B0"
        temp_condition_main.text = currentTemperatureData.weather.getJSONObject(0)
            .getString("main").toUpperCase(Locale.ENGLISH)

        temp_min_text.text = "${currentTemperatureData.main.getDouble("temp_min")}\u00B0\nmin"
        temp_curr_text.text = "${currentTemperatureData.main.getDouble("temp")}\u00B0\nCurrent"
        temp_max_text.text = "${currentTemperatureData.main.getDouble("temp_max")}\u00B0\nmax"

        curr_location.text = "${currentTemperatureData.name}, ${currentTemperatureData.sys.getString("country")}"
        curr_humidity.text = "HUMIDITY\n${currentTemperatureData.main.getDouble("humidity").roundToInt()}%"
        curr_pressure.text = "PRESSURE\n${currentTemperatureData.main.getDouble("pressure").roundToInt()}hPa"

        curr_feel.text = "FEELS LIKE\n${currentTemperatureData.main.getDouble("feels_like").roundToInt()}\u00B0"
        curr_visibility.text = "VISIBILITY\n${(currentTemperatureData.visibility/1000)}km"
//        location_text.text = "hello world"

        if(currentTemperatureData.weather.getJSONObject(0).getString("main").contains("cloud", true)) {

            background_main.background = ActivityCompat.getDrawable(requireContext(), R.drawable.forest_cloudy)
            main_layout.background = ActivityCompat.getDrawable(requireContext(), R.drawable.main_background_cloud)
        }
        else if(currentTemperatureData.weather.getJSONObject(0).getString("main").contains("clear", true)) {
            background_main.background = ActivityCompat.getDrawable(requireContext(), R.drawable.forest_sunny)
            main_layout.background = ActivityCompat.getDrawable(requireContext(), R.drawable.main_background_clear)
        }
        else if(currentTemperatureData.weather.getJSONObject(0).getString("main").contains("rain", true)) {
            background_main.background = ActivityCompat.getDrawable(requireContext(), R.drawable.forest_rainy)
            main_layout.background = ActivityCompat.getDrawable(requireContext(), R.drawable.main_background_rain)
        }

        /**
         * setting up adapter.
         */
        val forecastAdapter = ForecastAdapter(requireContext(), forecast)
        recycler_view_daily_forecast.adapter = forecastAdapter
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view_daily_forecast.layoutManager = layoutManager

    }

    private fun serializeToJSONObject(linkedTreeMap: LinkedTreeMap<Any, Any>?) : JSONObject? {
        val gson = Gson()
        val json : JsonObject = gson.toJsonTree(linkedTreeMap).asJsonObject

        println(json.javaClass)
        println(json)

        return JSONObject(json.toString())
    }

    fun convertLongToTime(time: Long?): String {
        if (time != null) {
            if (time > 0) {
                val date = Date(time)
                val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                return format.format(date)
            }
        }
        return ""
    }
}
