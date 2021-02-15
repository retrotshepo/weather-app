package za.co.weather.weather_app.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.content_main.*
import za.co.weather.weather_app.R
import za.co.weather.weather_app.util.CustomMapsHandler

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

        val mapsHandler = CustomMapsHandler(requireContext())
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map_frag) as SupportMapFragment
        supportMapFragment.getMapAsync(mapsHandler)
    }
}
