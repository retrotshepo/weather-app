package za.co.weather.weather_app.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import za.co.weather.weather_app.util.LocalDBHandler.Companion.createCurrent
import za.co.weather.weather_app.util.Util.Companion.isMobileDataConnected
import za.co.weather.weather_app.util.Util.Companion.isWiFiConnected
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

            when(isMobileDataConnected(requireContext()) || isWiFiConnected(requireContext())) {
                true -> {
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
                                            "${res.getInt("cod")}", System.currentTimeMillis()
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
                false -> {
                    Toast.makeText(requireContext(), getString(R.string.internet_not_stable), Toast.LENGTH_LONG).show()
                    txt_search_error.text = ""
                }
            }
        }

        btn_save.setOnClickListener {

            if (current == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.search_cannot_save),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.search_saved_new_city),
                    Toast.LENGTH_LONG
                ).show()
                createCurrent(current, arrayListOf(), "1")
                activity?.onBackPressed()
            }
        }
    }
}
