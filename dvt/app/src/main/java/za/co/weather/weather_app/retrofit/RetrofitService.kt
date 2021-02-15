package za.co.weather.weather_app.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import za.co.weather.weather_app.retrofit.NullOnEmptyConverterFactory.Companion.NullConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService {

    companion object {

        private const val BASE_URL_ROAD_WARRIOR = "https://api.openweathermap.org/"
        const val END_POINT_PREFIX = "data/2.5/"
        const val APP_ID = "230d8042e9393c5f5644efb4b4bcdf5e"

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
