package za.co.weather.weather_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.internal.GsonBuildConfig
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory
import za.co.weather.weather_app.util.WeatherAPIEndpoint.Companion.getWeatherCALL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_test.setOnClickListener {
            println("hello world!")

            this@MainActivity.lifecycleScope.launch (Dispatchers.IO) {

//                [-26.5603, 27.8625]
                val response = getWeatherCALL(-26.5603,27.8625)

//                JSONObject

                if (response != null) {

//                    val gg = JSONObject(response)
//                    gson.fromJson(theJson, JsonObject::class.java)

                    val gson = Gson()
                    val json = gson.toJsonTree(response).asJsonObject


                    println(json.javaClass)
                    println(json)

                }
            }
        }
    }
}