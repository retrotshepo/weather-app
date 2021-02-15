package za.co.weather.weather_app.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.content_main.*
import za.co.weather.weather_app.R

//class MapsScreen : Fragment() {
class MapsScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.main_toolbar?.visibility = ViewGroup.GONE







        return inflater.inflate(R.layout.layout_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (getString(R.string.google_api_key).isEmpty()) {
//            Toast.makeText(requireContext(), "Add your own API key in MapWithMarker/app/secure.properties as MAPS_API_KEY=YOUR_API_KEY", Toast.LENGTH_LONG).show()
//        }

        val mp = MapsMap(requireContext())

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map_frag) as SupportMapFragment

        supportMapFragment.getMapAsync(mp)


//        val mapFragment = activity?.supportFragmentManager?.findFragmentById(R.id.map_frag) as SupportMapFragment
//        mapFragment.getMapAsync(this)

//        val mapFragment = activity?.supportFragmentManager?.findFragmentById(R.id.map_frag) as? SupportMapFragment
//
//
//        mapFragment?.getMapAsync(object : OnMapReadyCallback {
//            override fun onMapReady(p0: GoogleMap) {
//                mMap = p0
//
//                // Add a marker in Sydney and move the camera
//                val sydney = LatLng(-34.0, 151.0)
//                mMap.addMarker(MarkerOptions()
//                    .position(sydney)
//                    .title("Marker in Sydney"))
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//            }
//        })
    }

    private lateinit var mMap: GoogleMap

//    override fun onMapReady(p0: GoogleMap) {
//        mMap = p0
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions()
//            .position(sydney)
//            .title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
}