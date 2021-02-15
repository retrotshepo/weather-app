package za.co.weather.weather_app.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import za.co.weather.weather_app.util.LocalDBHandler.Companion.readCurrent
import za.co.weather.weather_app.util.NavigationRoutes.Companion.reloadCurrentFragment
import za.co.weather.weather_app.util.Util
import za.co.weather.weather_app.util.Util.Companion.cityName
import za.co.weather.weather_app.util.Util.Companion.makeApiCall
import za.co.weather.weather_app.util.Util.Companion.updateScreen
import java.util.*

class FavouriteCity : Fragment() {

    private var current: CurrentTemperatureData? = null
    private var forecast: ArrayList<ForecastTemperatureData>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            if (Util.isWiFiConnected(requireContext()) || Util.isMobileDataConnected(requireContext())) {

                val pair = makeApiCall(requireActivity(), cityName)

            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.gps_not_enabled), Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()

        current = readCurrent(cityName)?.first
        forecast = readCurrent(cityName)?.second

        if (current != null && forecast != null) {
            updateScreen(activity as AppCompatActivity, current, forecast)
        }
    }
}
