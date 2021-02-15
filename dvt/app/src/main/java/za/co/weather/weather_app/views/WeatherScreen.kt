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
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.layout_weather_screen.*
import kotlinx.coroutines.runBlocking
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.model.ForecastTemperatureData
import za.co.weather.weather_app.util.CustomLocationListener
import za.co.weather.weather_app.util.LocalDBHandler.Companion.readCurrent
import za.co.weather.weather_app.util.NavigationRoutes.Companion.reloadCurrentFragment
import za.co.weather.weather_app.util.SharedPreferencesHandler.Companion.LATITUDE
import za.co.weather.weather_app.util.SharedPreferencesHandler.Companion.LONGITUDE
import za.co.weather.weather_app.util.SharedPreferencesHandler.Companion.getValue
import za.co.weather.weather_app.util.Util.Companion.isMobileDataConnected
import za.co.weather.weather_app.util.Util.Companion.isWiFiConnected
import za.co.weather.weather_app.util.Util.Companion.makeApiCall
import za.co.weather.weather_app.util.Util.Companion.updateScreen
import java.util.*

class WeatherScreen : Fragment() {

    private var current: CurrentTemperatureData? = null
    var forecast: ArrayList<ForecastTemperatureData>? = null
    var gps: CustomLocationListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gps = CustomLocationListener(requireContext())
        return inflater.inflate(R.layout.layout_weather_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.main_toolbar?.visibility = ViewGroup.GONE

        btn_refresh.setOnClickListener {
            reloadCurrentFragment(requireActivity() as AppCompatActivity, this)
        }

        burger_menu.setOnClickListener {
            activity?.weather_drawer_layout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onStart() = runBlocking {
        super.onStart()
        println("onStart")
            if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            if (isWiFiConnected(requireContext()) || isMobileDataConnected(requireContext())) {

//                val pair = makeApiCall(requireActivity(), gps?.getLatitude(), gps?.getLongitude())
//                val lat = getValue(LATITUDE)
//                val lon = getValue(LONGITUDE)
//
//
//                val lat1 = gps?.getLatitude()
//                val lon1 = gps?.getLongitude()


                when (!getValue(LATITUDE)?.equals(0.0F)!! || !getValue(LONGITUDE)?.equals(0.0F)!!) {
                    true -> {
                        val pair =
                            makeApiCall(requireActivity(), gps?.getLatitude(), gps?.getLongitude())
                    }

                    false -> {
                        val pair =
                            makeApiCall(requireActivity(), getValue(LATITUDE)?.toDouble(), getValue(LONGITUDE)?.toDouble())
                    }
                }
            }

            else {
                Toast.makeText(requireContext(), getString(R.string.internet_not_stable), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.gps_not_enabled), Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({

            current = readCurrent()?.first
            forecast = readCurrent()?.second

            if (current != null && !forecast.isNullOrEmpty() && current?.sys?.has("country")!!) {
                updateScreen(activity as AppCompatActivity, current, forecast)
            }
        }, 2000)
    }

    override fun onStop() {
        super.onStop()
        gps?.stopGPS()
    }
}
