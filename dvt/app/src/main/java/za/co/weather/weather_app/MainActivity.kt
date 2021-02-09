package za.co.weather.weather_app

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import za.co.weather.weather_app.util.LocalDBHandler.Companion.initialize
import za.co.weather.weather_app.util.SideNavigationOptions.Companion.favouritesScreen
import za.co.weather.weather_app.util.SideNavigationOptions.Companion.homeScreen
import kotlin.concurrent.fixedRateTimer


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var doubleBackToExitPressedOnce = false
    var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize("dvt-weather.db", applicationContext)

//        toggle = ActionBarDrawerToggle(
//            this, weather_drawer_layout, main_toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close)
    }

    override fun onResume() {
        super.onResume()
        if (supportFragmentManager.backStackEntryCount < 1) {
            homeScreen(this)
        }
    }

    override fun onStart() {
        super.onStart()

        nav_view.setNavigationItemSelectedListener(this)
        weather_drawer_layout?.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount <= 1) {

//            if (weather_drawer_layout.isDrawerOpen(GravityCompat.START)) {
//                weather_drawer_layout.closeDrawer(GravityCompat.START)
//                return
//            }

            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }

            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show()

            val h = Handler()
            h.postDelayed({
                doubleBackToExitPressedOnce = false
            }, 1500)
            return
        }

        super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when(id) {
            R.id.action_home -> {
                homeScreen(this)
            }
            R.id.action_favourite_locations -> {
                favouritesScreen(this)
            }
        }


//        if (id == R.id.action_home) {
//            homeScreen(this)
//        }
//        else if (id == R.id.action_favourite_locations) {
//            favouritesScreen(this)
//        }

        weather_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
