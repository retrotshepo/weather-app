package za.co.weather.weather_app.views

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsMap(var context: Context) : OnMapReadyCallback {

    var mGoogleMap: GoogleMap? = null

    override fun onMapReady(p0: GoogleMap?) {
        mGoogleMap = p0

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-26.1942764, 28.0049734)
        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap?.isMyLocationEnabled = true
        }
    }

}
