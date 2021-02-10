package za.co.weather.weather_app.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.layout_search_city.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.retrofit.WeatherAPIEndpoint.Companion.getWeatherCityCALL
import za.co.weather.weather_app.util.LocalDBHandler.Companion.create
import za.co.weather.weather_app.util.Util.Companion.serializeToJSONObject

class SearchCity : Fragment() {

    private var current: CurrentTemperatureData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_search_city, container, false)
    }

    override fun onResume() {
        super.onResume()

        btn_search.setOnClickListener {
            txt_search_error.visibility = ViewGroup.VISIBLE
            txt_search_error.setTextColor(ContextCompat.getColor(requireActivity(), R.color.amber))
            txt_search_error.text = getString(R.string.search_waiting)

            this@SearchCity.lifecycleScope.launch(Dispatchers.IO) {

                try {
                    val resCurrent = async { getWeatherCityCALL(text_city.text.toString()) }.await()

                    if (resCurrent != null) {

                        val res = serializeToJSONObject(resCurrent as LinkedTreeMap<Any, Any>)

                        if (res != null) {
                            current =
                                CurrentTemperatureData(
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
                            txt_search_error.text = getString(R.string.search_found)
                            txt_search_error.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.green
                                )
                            )
                        }

                    } else {
                        this@SearchCity.lifecycleScope.launch(Dispatchers.Main) {
                            txt_search_error.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.red
                                )
                            )
                            txt_search_error.text = getString(R.string.search_not_found)
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message)
                    if (e.code() == 404) {
                        this@SearchCity.lifecycleScope.launch(Dispatchers.Main) {
//                        activity?.onBackPressed()
                            txt_search_error.setTextColor(
                                ContextCompat
                                    .getColor(requireActivity(), R.color.red)
                            )
                            txt_search_error.text = e.message

                        }
                    }
                }
            }
        }

        btn_save.setOnClickListener {
            create(current)
            activity?.onBackPressed()
        }
    }
}
