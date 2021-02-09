package za.co.weather.weather_app.views

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.layout_search_city.*
import kotlinx.android.synthetic.main.layout_weather_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import za.co.weather.weather_app.R
import za.co.weather.weather_app.util.CustomLocationListener
import za.co.weather.weather_app.util.LocalDBHandler.Companion.create
import za.co.weather.weather_app.util.WeatherAPIEndpoint
import za.co.weather.weather_app.util.WeatherAPIEndpoint.Companion.getWeatherCityCALL
import za.co.weather.weather_app.util.WeatherAPIEndpoint.Companion.getWeatherCityForecastCALL

class SearchCity : Fragment() {

    private var current: CurrentTemperatureData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_search_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("SearchCity onViewCreated")

    }

    override fun onResume() {
        super.onResume()

        btn_search.setOnClickListener {


            this@SearchCity.lifecycleScope.launch(Dispatchers.IO) {
            val resCurrent = getWeatherCityCALL(text_city.text.toString())

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
                this@SearchCity.lifecycleScope.launch(Dispatchers.Main) {
                txt_search_error.text = ""
                }
            }

            else {
                this@SearchCity.lifecycleScope.launch(Dispatchers.Main) {

                txt_search_error.visibility = ViewGroup.VISIBLE
                txt_search_error.text = "not found"
                }
            }
        }

//            Handler().postDelayed({
//                if(current == null) {
//                    txt_search_error.visibility = ViewGroup.VISIBLE
//                txt_search_error.text = "not found"
//                }
//                else{
//                txt_search_error.text = ""
//
//                }
//            }, 2000)
        }

        btn_save.setOnClickListener {
            create(current)
            activity?.onBackPressed()
        }
    }


    private fun serializeToJSONObject(linkedTreeMap: LinkedTreeMap<Any, Any>?) : JSONObject? {
        val gson = Gson()
        val json : JsonObject = gson.toJsonTree(linkedTreeMap).asJsonObject

        println(json.javaClass)
        println(json)

        return JSONObject(json.toString())
    }

}