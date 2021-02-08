package za.co.weather.weather_app.views

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import za.co.weather.weather_app.util.WeatherAPIEndpoint
import java.util.*

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
            apitest()
        }
    }







    fun apitest() {
        println("hello world!")

        this@WeatherScreen.lifecycleScope.launch (Dispatchers.IO) {

//                [-26.5603, 27.8625]
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
                        "${res.getInt("cod")}")

                }

                println(res)

            }

            if (resForecast != null) {

                val res = serializeToJSONObject(resForecast as LinkedTreeMap<Any, Any>)

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

        temp_number_main.text = "${currentTemperatureData.main.getDouble("temp")}"
        temp_condition_main.text = currentTemperatureData.weather.getJSONObject(0)
            .getString("main").toUpperCase(Locale.ENGLISH)

        temp_min_text.text = "${currentTemperatureData.main.getDouble("temp_min")}\nmin"
        temp_curr_text.text = "${currentTemperatureData.main.getDouble("feels_like")}\nFeels like"
        temp_max_text.text = "${currentTemperatureData.main.getDouble("temp_max")}\nmax"

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
}
