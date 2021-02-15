package za.co.weather.weather_app.util

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

//class MapHandler: OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
class MapHandler(var context: Context): OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    var gps: CustomLocationListener? = null
    var googleMap: GoogleMap? = null

//    var googleApiClient: GoogleApiClient? = null
    var googleApiClient: GoogleApiClient? = null

    var mLocationRequest: LocationRequest? = null

    var marker: Marker? = null


    override fun onMapReady(p0: GoogleMap?) {

    }


    fun setUpClient() {

        googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        googleApiClient?.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.setPriority(LocationRequest.PRIORITY_LOW_POWER)?.setPriority(120000)


//        placeMarkers(locations)

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    fun placeMarkers(l: ArrayList<String>) {
        println("locations size: " + l.size)
        for (plek in l) {
            if (marker != null) {

            }
            val options = MarkerOptions()
                .title(plek)
                .snippet("luggagemate demo")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                .position(LatLng(plek.getLat(), plek.getLng()))
            marker = googleMap?.addMarker(options)
        }
        Toast.makeText(context, "Map ready", Toast.LENGTH_LONG).show()
    }
}