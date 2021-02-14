package za.co.weather.weather_app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import za.co.weather.weather_app.util.LocalDBHandler.Companion.initialize
import za.co.weather.weather_app.util.NavigationRoutes.Companion.favouritesScreen
import za.co.weather.weather_app.util.NavigationRoutes.Companion.homeScreen
import za.co.weather.weather_app.util.SharedPreferencesHandler.Companion.initPref


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var doubleBackToExitPressedOnce = false
    private val permissionsRequestCode = 10
    private val sensitivePermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,   Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize("dvt-weather.db", applicationContext)
        grantMultiplePermissions()
    }

    override fun onResume() {
        super.onResume()
        initPref(applicationContext)
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

            if (weather_drawer_layout.isDrawerOpen(GravityCompat.START)) {
                weather_drawer_layout.closeDrawer(GravityCompat.START)
                return
            }

            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }

            doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.main_quit_app), Toast.LENGTH_SHORT).show()

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

        weather_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            permissions.forEach { perm ->
                if (ActivityCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun grantMultiplePermissions() {
        if (!hasPermissions(this, sensitivePermissions)) {
            ActivityCompat.requestPermissions(this, sensitivePermissions, permissionsRequestCode)
        }
    }
}
