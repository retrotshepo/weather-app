package za.co.weather.weather_app.util

import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import za.co.weather.weather_app.util.CallValues.Companion.UNITS
import za.co.weather.weather_app.util.RetrofitService.Companion.BASE_URL_ROAD_WARRIOR

interface WeatherAPIDataService {

    @GET("data/2.5/forecast?appid=230d8042e9393c5f5644efb4b4bcdf5e&units=metric&mode=json&cnt=40")
    suspend fun getWeatherDataAPI(@Query("lat") lat: Double, @Query("lon") lon: Double): Any

}