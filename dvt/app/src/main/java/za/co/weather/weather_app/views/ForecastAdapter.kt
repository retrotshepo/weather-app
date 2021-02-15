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
import za.co.weather.weather_app.model.ForecastTemperatureData
import za.co.weather.weather_app.util.Util.Companion.convertDateToDay
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.roundToInt

class ForecastAdapter(private var context: Context, items: ArrayList<ForecastTemperatureData>?) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    private var items: ArrayList<ForecastTemperatureData>

    init {
        this.items = arrayListOf()
        if (!items.isNullOrEmpty()) {
            this.items = items
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_forecast_day, parent, false)
        return ViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.dailyTempMax.get()?.text = "${item.main.getDouble("temp_max").roundToInt()}\u00B0"
        holder.dailyTempDay.get()?.text = "${convertDateToDay(item.date)}"

        when (item.weather.getJSONObject(0).getString("main").contains("cloud", true)) {
            true -> {
                holder.dailyTempIcon.get()
                    ?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.partlysunny))
            }
            false -> {
                when (item.weather.getJSONObject(0).getString("main").contains("clear", true)) {
                    true -> {
                        holder.dailyTempIcon.get()
                            ?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.clear))
                    }
                    false -> {
                        when (item.weather.getJSONObject(0).getString("main")
                            .contains("rain", true)) {
                            true -> {
                                holder.dailyTempIcon.get()
                                    ?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rain))
                            }
                        }
                    }
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dailyTempDay: WeakReference<TextView> = WeakReference(itemView.findViewById(R.id.item_forecast_day))
        var dailyTempIcon: WeakReference<ImageView> = WeakReference(itemView.findViewById(R.id.item_forecast_condition))
        var dailyTempMax: WeakReference<TextView> = WeakReference(itemView.findViewById(R.id.item_forecast_temp))

    }
}
