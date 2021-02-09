package za.co.weather.weather_app.views

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.layout_weather_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import za.co.weather.weather_app.R
import za.co.weather.weather_app.util.CustomLocationListener
import za.co.weather.weather_app.util.LocalDBHandler.Companion.create
import za.co.weather.weather_app.util.NetworkState.Companion.isMobileDataConnected
import za.co.weather.weather_app.util.NetworkState.Companion.isWiFiConnected
import za.co.weather.weather_app.util.WeatherAPIEndpoint
import java.util.*
import kotlin.math.roundToInt

class WeatherScreen : Fragment() {

    private var current: CurrentTemperatureData? = null
    var forecast = arrayListOf<DailyTemperatureData>()
    var gps: CustomLocationListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        gps = CustomLocationListener()

        gps?.getLastKnownLocationGPS(requireContext())
        gps?.getLastKnownLocationNetwork(requireContext())

        return inflater.inflate(R.layout.layout_weather_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("WeatherScreen onViewCreated")



            callWeatherEndpoints(gps?.getLatitude() , gps?.getLongitude())

        temp_day_container.setOnClickListener {
            println("tuts een twe")
        }
    }


    fun callWeatherEndpoints(lat: Double?, lon: Double?) {
        println("api test\tlat: $lat ; lon: $lon")

        if (lat == null || lon == null) {
            return
        }

        if(!isMobileDataConnected(requireContext()) && !isWiFiConnected(requireContext())) {
            Toast.makeText(requireContext(), "No stable internet connection to update", Toast.LENGTH_LONG).show()
            return
        }

        this@WeatherScreen.lifecycleScope.launch (Dispatchers.IO) {

//                [-26.5603, 27.8625] orange farm
//            -26.1846304,28.0014622 ACK
//            -33.9152208,18.3758763 CPT
//            -30.7158878,30.3887406 NPS
            val resCurrent = WeatherAPIEndpoint.getWeatherCurrentCALL(lat, lon)
            val resForecast = WeatherAPIEndpoint.getWeatherForecastCALL(lat, lon)

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
        }, 3500)
    }

    private fun updateScreen(currentTemperatureData: CurrentTemperatureData?) {

        if(currentTemperatureData == null || temp_number_main == null) {
            return
        }

//        create(currentTemperatureData)

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
        recycler_view_daily_forecast.visibility = ViewGroup.VISIBLE
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


    override fun onStop() {
        super.onStop()
        gps?.stopGPS()
    }
}
