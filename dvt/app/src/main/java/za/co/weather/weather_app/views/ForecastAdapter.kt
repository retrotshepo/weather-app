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
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

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

        holder.dailyTempMax.get()?.text = "${item.main.getDouble("temp_max").roundToInt()}"
//        holder.dailyTempDay.get()?.text = item.date
        holder.dailyTempDay.get()?.text = "${convertDateToDay(item.date)}".toLowerCase(Locale.ENGLISH).capitalize()

        if(item.weather.getJSONObject(0).getString("main").contains("cloud", true)) {
            holder.dailyTempIcon.get()?.setImageDrawable(context.getDrawable(R.drawable.partlysunny))
        }
        else if(item.weather.getJSONObject(0).getString("main").contains("clear", true)) {
            holder.dailyTempIcon.get()?.setImageDrawable(context.getDrawable(R.drawable.clear))

        }
        else if(item.weather.getJSONObject(0).getString("main").contains("rain", true)) {
            holder.dailyTempIcon.get()?.setImageDrawable(context.getDrawable(R.drawable.rain))
        }



//        holder.bind(item)
    }

    fun convertDateToDay(date: String): DayOfWeek? {

        val calendar = Calendar.getInstance()

//        var simpleFormat =  DateTimeFormatter.ISO_DATE;
//        val todayFormat = SimpleDateFormat("HH:mm \tEEEE, dd/MMMM/yyyy ")

        val todayFormat = SimpleDateFormat("dd/MMMM/yyyy HH:mm:ss")

        var convertedDate = LocalDate.parse(date.substring(0, 10), DateTimeFormatter.ISO_DATE)

        return convertedDate.dayOfWeek

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
