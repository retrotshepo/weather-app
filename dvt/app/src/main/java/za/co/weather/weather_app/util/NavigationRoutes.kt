package za.co.weather.weather_app.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import za.co.weather.weather_app.R
import za.co.weather.weather_app.views.FavouriteCity
import za.co.weather.weather_app.views.Favourites
import za.co.weather.weather_app.views.SearchCity
import za.co.weather.weather_app.views.WeatherScreen

class NavigationRoutes {

    companion object {

        fun homeScreen(context: AppCompatActivity) {
            loadFragment(context, WeatherScreen())
        }

        fun favouritesScreen(context: AppCompatActivity) {
            loadFragment(context, Favourites())
        }

        fun cityScreen(context: AppCompatActivity) {
            loadFragment(context, FavouriteCity())
        }

        fun searchScreen(context: AppCompatActivity) {
            loadFragment(context, SearchCity())
        }

        private fun loadFragment(activity: AppCompatActivity, fragment: Fragment, holder: Int = R.id.main_content_frame) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.addToBackStack(fragment.javaClass.simpleName)

            transaction.replace(holder, fragment)
            transaction.commit()
        }

        fun reloadCurrentFragment(activity: AppCompatActivity, fragment: Fragment) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.detach(fragment)
            transaction.attach(fragment)
            transaction.commit()
        }
    }
}
