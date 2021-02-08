package za.co.weather.weather_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import za.co.weather.weather_app.util.SideNavigationOptions.Companion.homeScreen


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeScreen(this)
    }


}
