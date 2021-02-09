package za.co.weather.weather_app.views

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_favourites.*
import kotlinx.android.synthetic.main.layout_favourites.*
import kotlinx.android.synthetic.main.layout_weather_screen.*
import za.co.weather.weather_app.R
import za.co.weather.weather_app.util.CustomLocationListener
import za.co.weather.weather_app.util.LocalDBHandler.Companion.delete
import za.co.weather.weather_app.util.LocalDBHandler.Companion.read
import za.co.weather.weather_app.util.SideNavigationOptions.Companion.cityScreen
import za.co.weather.weather_app.util.SideNavigationOptions.Companion.searchScreen

class Favourites : Fragment() {

    var alertDialog: AlertDialog? = null

//    override fun onCreateView(inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("WeatherScreen onViewCreated")

        val all = read()

        /**
         * setting up adapter.
         */
        val forecastAdapter = FavouritesAdapter(requireContext(), all, object : FavouritesAdapter.OnItemClickListener{
            override fun onItemClicked(current: CurrentTemperatureData) {

                Toast.makeText(requireContext(), "${current.name}\t${current.sys.getString("country")}", Toast.LENGTH_LONG).show()
                method(current.name, current.sys.getString("country"))


            }
        })

        recycler_view_favourites.visibility = ViewGroup.VISIBLE
        recycler_view_favourites.adapter = forecastAdapter
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view_favourites.layoutManager = layoutManager

    }

    override fun onResume() {
        super.onResume()

        fav_floating_button.setOnClickListener {

            searchScreen(requireActivity() as AppCompatActivity)
        }
    }

    override fun onPause() {
        super.onPause()
        if (alertDialog != null) {
            alertDialog?.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        if (alertDialog != null) {
            alertDialog?.dismiss()
        }
    }

    fun method(city: String, country: String) {


        alertDialog = (activity as AppCompatActivity).let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("View",
                    DialogInterface.OnClickListener { dialog, id ->
                        cityScreen(it)
                    })
                setNegativeButton("Remove",
                    DialogInterface.OnClickListener { dialog, id ->
                        delete(city, country)
                    })
//                setNeutralButton("",
//                    DialogInterface.OnClickListener { dialog, id ->
//
//                })

                setTitle("Selection")
                setMessage("Location")
            }
            builder.create()
        }

        alertDialog?.setCancelable(false)
        alertDialog?.show()

    }


}