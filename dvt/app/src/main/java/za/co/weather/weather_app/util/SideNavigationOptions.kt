package za.co.weather.weather_app.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import za.co.weather.weather_app.R
import za.co.weather.weather_app.views.Favourites
import za.co.weather.weather_app.views.WeatherScreen

class SideNavigationOptions {

    companion object {

        fun homeScreen(context: AppCompatActivity) {
            loadFragment(context, WeatherScreen())
        }

        fun favouritesScreen(context: AppCompatActivity) {
            loadFragment(context, Favourites())

        }

        private fun loadFragment(activity: AppCompatActivity, fragment: Fragment, holder: Int = R.id.main_content_frame) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.addToBackStack(fragment.javaClass.simpleName)

            transaction.replace(holder, fragment)
            transaction.commit()
        }
    }
}
