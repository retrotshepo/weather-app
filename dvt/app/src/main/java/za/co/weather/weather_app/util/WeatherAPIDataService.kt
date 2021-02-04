package za.co.weather.weather_app.util

import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WeatherAPIDataService {

    @GET("")
    suspend fun getWeatherDataAPI(): JSONObject

}