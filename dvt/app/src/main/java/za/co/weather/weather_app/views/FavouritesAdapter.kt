package za.co.weather.weather_app.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.CurrentTemperatureData
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.roundToInt

class FavouritesAdapter(
    private var context: Context,
    private var items: ArrayList<CurrentTemperatureData>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite_location, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.favouriteCity.get()?.text = "${item.name}, ${item.sys.getString("country")}"
        holder.favouriteMinMax.get()?.text =
            "${item.main.getDouble("temp_max").roundToInt()}\u00B0 / ${item.main.getDouble("temp_min").roundToInt()}\u00B0"
        holder.favouriteCurrent.get()?.text = "${item.main.getDouble("temp").roundToInt()}\u00B0"

        when (item.weather.getJSONObject(0).getString("main").contains("cloud", true)) {
            true -> {
                holder.favouriteIcon.get()
                    ?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.partlysunny))
            }
            false -> {
                when (item.weather.getJSONObject(0).getString("main").contains("clear", true)) {
                    true -> {
                        holder.favouriteIcon.get()
                            ?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.clear))
                    }
                    false -> {
                        when (item.weather.getJSONObject(0).getString("main")
                            .contains("rain", true) || item.weather.getJSONObject(0).getString("main")
                            .contains("drizzle", true)) {
                            true -> {
                                holder.favouriteIcon.get()
                                    ?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rain))
                            }
                        }
                    }
                }
            }
        }

        holder.bind(item, listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var favouriteCity: WeakReference<TextView> = WeakReference(itemView.findViewById(R.id.item_fav_city))
        var favouriteIcon: WeakReference<ImageView> = WeakReference(itemView.findViewById(R.id.item_fav_icon))
        var favouriteMinMax: WeakReference<TextView> = WeakReference(itemView.findViewById(R.id.item_fav_high_low))
        var favouriteCurrent: WeakReference<TextView> = WeakReference(itemView.findViewById(R.id.item_fav_current))

        fun bind(current: CurrentTemperatureData, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(current)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClicked(current: CurrentTemperatureData)
    }
}
