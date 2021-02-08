package za.co.weather.weather_app.util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import za.co.weather.weather_app.util.NullOnEmptyConverterFactory.Companion.NullConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService {

    companion object {

        private const val BASE_URL_ROAD_WARRIOR = "https://api.openweathermap.org/"


        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()

        fun getClient(): WeatherAPIDataService? {

            val refit = Retrofit.Builder()
                .baseUrl(BASE_URL_ROAD_WARRIOR)
                .addConverterFactory(NullConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return refit.create(WeatherAPIDataService::class.java)
        }

    }
}
