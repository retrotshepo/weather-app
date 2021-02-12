package za.co.weather.weather_app.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import za.co.weather.weather_app.R
import za.co.weather.weather_app.model.DailyTemperatureData
import za.co.weather.weather_app.util.Util.Companion.convertDateToDay
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private var context: Context
    private var items: ArrayList<DailyTemperatureData>

    constructor(
        context: Context,
        items: ArrayList<DailyTemperatureData>?
    ) : super() {
        this.context = context
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
                    ?.setImageDrawable(context.getDrawable(R.drawable.partlysunny))
            }
            false -> {
                when (item.weather.getJSONObject(0).getString("main").contains("clear", true)) {
                    true -> {
                        holder.dailyTempIcon.get()
                            ?.setImageDrawable(context.getDrawable(R.drawable.clear))
                    }
                    false -> {
                        when (item.weather.getJSONObject(0).getString("main")
                            .contains("rain", true)) {
                            true -> {
                                holder.dailyTempIcon.get()
                                    ?.setImageDrawable(context.getDrawable(R.drawable.rain))
                            }
                        }
                    }
                }
            }
        }
    }
//
//    private fun convertDateToDay(date: String): String? {
//
//        val calendar = Calendar.getInstance()
//        val todayFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
//        calendar.time = todayFormat.parse(date)
//
//        return getDayString(calendar.get(Calendar.DAY_OF_WEEK))
//    }
//
//
//    private fun getDayString(day: Int): String? {
//        when (day) {
//            1 -> { return "Monday" }
//            2 -> { return "Tuesday" }
//            3 -> { return "Wednesday" }
//            4 -> { return "Thursday" }
//            5 -> { return "Friday" }
//            6 -> { return "Saturday" }
//            7 -> { return "Sunday" }
//        }
//        return ""
//    }

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
