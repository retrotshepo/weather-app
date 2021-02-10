package za.co.weather.weather_app.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.CurrentTemperatureData
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.roundToInt

class FavouritesAdapter : RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private var context: Context
    private var items: ArrayList<CurrentTemperatureData>

    private var listener: OnItemClickListener

    constructor(
        context: Context,
        items: ArrayList<CurrentTemperatureData>,
        listener: OnItemClickListener

    ) : super() {
        this.context = context
        this.items = items
        this.listener = listener
    }

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

        holder.favouriteCity.get()?.text = "${item.name}"
        holder.favouriteMinMax.get()?.text = "${item.main.getDouble("temp_max")
            .roundToInt()}\u00B0 / ${item.main.getDouble("temp_min").roundToInt()}\u00B0"
        holder.favouriteCurrent.get()?.text = "${item.main.getDouble("temp").roundToInt()}\u00B0"

        when (item.weather.getJSONObject(0).getString("main").contains("cloud", true)) {
            true -> {
                holder.favouriteIcon.get()
                    ?.setImageDrawable(context.getDrawable(R.drawable.partlysunny))
            }
            false -> {
                when (item.weather.getJSONObject(0).getString("main").contains("clear", true)) {
                    true -> {
                        holder.favouriteIcon.get()
                            ?.setImageDrawable(context.getDrawable(R.drawable.clear))
                    }
                    false -> {
                        when (item.weather.getJSONObject(0).getString("main")
                            .contains("rain", true)) {
                            true -> {
                                holder.favouriteIcon.get()
                                    ?.setImageDrawable(context.getDrawable(R.drawable.rain))
                            }
                        }
                    }
                }
            }
        }

        holder.bind(item, listener)
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var favouriteCity: WeakReference<TextView>
        var favouriteIcon: WeakReference<ImageView>
        var favouriteMinMax: WeakReference<TextView>
        var favouriteCurrent: WeakReference<TextView>

        constructor(itemView: View) : super(itemView) {
            favouriteCity = WeakReference(itemView.findViewById(R.id.item_fav_city))
            favouriteCurrent = WeakReference(itemView.findViewById(R.id.item_fav_current))
            favouriteMinMax = WeakReference(itemView.findViewById(R.id.item_fav_high_low))
            favouriteIcon = WeakReference(itemView.findViewById(R.id.item_fav_icon))
        }

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
