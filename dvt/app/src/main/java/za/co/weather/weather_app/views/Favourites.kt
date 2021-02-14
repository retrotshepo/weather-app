package za.co.weather.weather_app.views

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_favourites.*
import kotlinx.android.synthetic.main.layout_favourites.*
import kotlinx.android.synthetic.main.content_main.*
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.util.LocalDBHandler.Companion.deleteCurrent
import za.co.weather.weather_app.util.LocalDBHandler.Companion.readFavourites
import za.co.weather.weather_app.util.NavigationRoutes.Companion.cityScreen
import za.co.weather.weather_app.util.NavigationRoutes.Companion.reloadCurrentFragment
import za.co.weather.weather_app.util.NavigationRoutes.Companion.searchScreen
import za.co.weather.weather_app.util.Util.Companion.cityName

class Favourites : Fragment() {

    var alertDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.main_toolbar?.visibility = ViewGroup.VISIBLE

        return inflater.inflate(R.layout.layout_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = getString(R.string.screen_favourites)
        setHasOptionsMenu(true)

    }

    override fun onStart() {
        super.onStart()


        val favourites = readFavourites()
        val forecastAdapter = FavouritesAdapter(
            requireContext(),
            favourites,
            object : FavouritesAdapter.OnItemClickListener {
                override fun onItemClicked(current: CurrentTemperatureData) {
                    launchDialog(current.name, current.sys.getString("country"))
                }
            })

        when(favourites.isNullOrEmpty()) {
            true -> {
                recycler_view_favourites.visibility = ViewGroup.GONE
                fav_no_faves.visibility = ViewGroup.VISIBLE
            }
            false -> {
                recycler_view_favourites.visibility = ViewGroup.VISIBLE
                fav_no_faves.visibility = ViewGroup.GONE
            }
        }

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

    fun launchDialog(city: String, country: String) {

        alertDialog = (activity as AppCompatActivity).let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("Open",
                    DialogInterface.OnClickListener { dialog, id ->
                        cityName = city
                        cityScreen(it)
                    })
                setNegativeButton("Remove",
                    DialogInterface.OnClickListener { dialog, id ->
                        deleteCurrent(city, country)
                        reloadCurrentFragment(it, this@Favourites)
                    })
                setNeutralButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        alertDialog?.dismiss()
                    })
                setTitle("Selected City")
                setMessage("$city, $country")
            }
            builder.create()
        }

        alertDialog?.setCancelable(false)
        alertDialog?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.menu_favourites_overflow, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_view_locations) {
            println("hello world")
        }

        return super.onOptionsItemSelected(item)

    }

}
