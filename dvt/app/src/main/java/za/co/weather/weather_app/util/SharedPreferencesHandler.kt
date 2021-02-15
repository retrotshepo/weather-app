package za.co.weather.weather_app.util

import android.content.Context
import android.content.SharedPreferences
import za.co.weather.weather_app.R

class SharedPreferencesHandler {

    companion object {

        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"

        private var sharedPreference: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null

        fun initPref(context: Context) {
            sharedPreference = context.getSharedPreferences(
                context.getString(R.string.weather_shared_prefs),
                Context.MODE_PRIVATE
            )
        }

        fun saveValue(key: String, value: Float?) {

            if(value == null) {
                return
            }
            editor = sharedPreference?.edit()
            editor?.putFloat(key, value)
            editor?.apply()
        }

        fun getValue(key: String): Float? {
            return sharedPreference?.getFloat(key, 0.0F)
        }


        fun dropPreferences() {
            editor = sharedPreference?.edit()
            editor?.clear()
            editor?.apply()
        }

    }
}