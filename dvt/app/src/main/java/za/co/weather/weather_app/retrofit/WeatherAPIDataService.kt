package za.co.weather.weather_app.util

import retrofit2.http.GET
import retrofit2.http.Query
import za.co.weather.weather_app.retrofit.RetrofitService.Companion.END_POINT_PREFIX

interface WeatherAPIDataService {

    @GET("${END_POINT_PREFIX}weather?appid=230d8042e9393c5f5644efb4b4bcdf5e&units=metric&mode=json")
    suspend fun getCurrentWeatherDataAPI(@Query("lat") lat: Double, @Query("lon") lon: Double): Any

    @GET("${END_POINT_PREFIX}forecast?appid=230d8042e9393c5f5644efb4b4bcdf5e&units=metric&mode=json&cnt=40")
    suspend fun getWeatherForecastDataAPI(@Query("lat") lat: Double, @Query("lon") lon: Double): Any

    @GET("${END_POINT_PREFIX}weather?&appid=74b118a2e76bfa7bee7ed2941525f768&units=metric")
    suspend fun getWeatherCityAPI(@Query("q") city: String = "Soweto"): Any

    @GET("${END_POINT_PREFIX}forecast?&appid=74b118a2e76bfa7bee7ed2941525f768&units=metric&cnt=40")
    suspend fun getWeatherCityForecastAPI(@Query("q") city: String = "Soweto"): Any
}
