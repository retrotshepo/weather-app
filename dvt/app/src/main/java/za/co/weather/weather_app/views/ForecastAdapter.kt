package za.co.weather.weather_app.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import za.co.weather.weather_app.R
import java.lang.ref.WeakReference

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private var context: Context
    private var items: ArrayList<DailyTemperatureData>

    constructor(
        context: Context,
        items: ArrayList<DailyTemperatureData>
    ) : super() {
        this.context = context
        this.items = items
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

        holder.dailyTempMax.get()?.text = (item.main.getDouble("temp_max")).toString()
        holder.dailyTempDay.get()?.text = item.date

//        holder.bind(item)
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var dailyTempDay: WeakReference<TextView>
        var dailyTempIcon: WeakReference<ImageView>
        var dailyTempMax: WeakReference<TextView>

        constructor(itemView: View) : super(itemView) {
            dailyTempDay = WeakReference(itemView.findViewById(R.id.item_forecast_day))
            dailyTempMax = WeakReference(itemView.findViewById(R.id.item_forecast_temp))
            dailyTempIcon = WeakReference(itemView.findViewById(R.id.item_forecast_condition))
        }

    }
}
