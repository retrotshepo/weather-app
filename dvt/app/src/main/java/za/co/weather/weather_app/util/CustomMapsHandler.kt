package za.co.weather.weather_app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import za.co.weather.weather_app.util.LocalDBHandler.Companion.readFavourites
import kotlin.math.roundToInt

class CustomMapsHandler(var context: Context) : OnMapReadyCallback {

    var mGoogleMap: GoogleMap? = null

    override fun onMapReady(p0: GoogleMap?) {
        mGoogleMap = p0
        readFavourites().forEach {item ->
            mGoogleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(item.coordinates.getDouble("lat"),item.coordinates.getDouble("lon")))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("${item.name}, ${item.sys.getString("country")}")
                    .snippet("Expect ${item.weather.getJSONObject(0)
                        .getString("description")}, with temperatures peaking at ${item.main.getDouble("temp_max").roundToInt()}\u00B0")
            )
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap?.isMyLocationEnabled = true
        }
    }
}
