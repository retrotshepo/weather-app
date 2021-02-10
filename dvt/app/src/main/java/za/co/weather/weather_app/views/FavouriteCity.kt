package za.co.weather.weather_app.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_weather_screen.*
import kotlinx.coroutines.runBlocking
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.model.DailyTemperatureData
import za.co.weather.weather_app.util.LocalDBHandler.Companion.update
import za.co.weather.weather_app.util.NavigationRoutes
import za.co.weather.weather_app.util.Util
import za.co.weather.weather_app.util.Util.Companion.cityName
import za.co.weather.weather_app.util.Util.Companion.makeApiCall
import java.util.*
import kotlin.math.roundToInt

class FavouriteCity : Fragment() {

    private var current: CurrentTemperatureData? = null
    private var forecast: ArrayList<DailyTemperatureData>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_weather_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_refresh.setOnClickListener {
            NavigationRoutes.reloadCurrentFragment(requireActivity() as AppCompatActivity, this)
        }

        burger_menu.setOnClickListener {
            activity?.weather_drawer_layout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onResume() = runBlocking {
        super.onResume()

        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            if (Util.isWiFiConnected(requireContext()) || Util.isMobileDataConnected(requireContext())) {

                val pair = makeApiCall(requireActivity(), cityName)
                current = pair.first
                forecast = pair.second

                if (current != null && forecast != null) {
                    updateScreen(current)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please grant GPS permission", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun updateScreen(currentTemperatureData: CurrentTemperatureData?) {

        if(currentTemperatureData == null || temp_number_main == null) {
            return
        }

        update(cityName, currentTemperatureData)

        temp_number_main.text = "${currentTemperatureData.main.getDouble("temp").roundToInt()}\u00B0"
        temp_condition_main.text = currentTemperatureData.weather.getJSONObject(0)
            .getString("main").toUpperCase(Locale.ENGLISH)

        temp_min_text.text = "${currentTemperatureData.main.getDouble("temp_min").roundToInt()}\u00B0\nmin"
        temp_curr_text.text = "${currentTemperatureData.main.getDouble("temp").roundToInt()}\u00B0\nCurrent"
        temp_max_text.text = "${currentTemperatureData.main.getDouble("temp_max").roundToInt()}\u00B0\nmax"

        curr_location.text = "${currentTemperatureData.name}, ${currentTemperatureData.sys.getString("country")}"
        curr_humidity.text = "HUMIDITY\n${currentTemperatureData.main.getDouble("humidity").roundToInt()}%"
        curr_pressure.text = "PRESSURE\n${currentTemperatureData.main.getDouble("pressure").roundToInt()}hPa"

        curr_feel.text = "FEELS LIKE\n${currentTemperatureData.main.getDouble("feels_like").roundToInt()}\u00B0"
        curr_visibility.text = "VISIBILITY\n${(currentTemperatureData.visibility/1000)}km"

        when(currentTemperatureData.weather.getJSONObject(0).getString("main").contains("cloud", true)) {

            true -> {
                background_main.background = ActivityCompat.getDrawable(requireContext(), R.drawable.forest_cloudy)
                main_layout.background = ActivityCompat.getDrawable(requireContext(), R.drawable.main_background_cloud)
            }
            false -> {
                when(currentTemperatureData.weather.getJSONObject(0).getString("main").contains("clear", true)) {

                    true -> {
                        background_main.background = ActivityCompat.getDrawable(requireContext(), R.drawable.forest_sunny)
                        main_layout.background = ActivityCompat.getDrawable(requireContext(), R.drawable.main_background_clear)
                    }
                    false -> {
                        when(currentTemperatureData.weather.getJSONObject(0).getString("main").contains("rain", true)) {
                            true -> {
                                background_main.background = ActivityCompat.getDrawable(requireContext(), R.drawable.forest_rainy)
                                main_layout.background = ActivityCompat.getDrawable(requireContext(), R.drawable.main_background_rain)
                            }
                            false -> { }
                        }
                    }
                }
            }
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
}
