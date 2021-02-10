package za.co.weather.weather_app.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import za.co.weather.weather_app.retrofit.RetrofitService.Companion.APP_ID
import za.co.weather.weather_app.retrofit.RetrofitService.Companion.END_POINT_PREFIX

interface WeatherAPIDataService {

    @GET("${END_POINT_PREFIX}weather?appid=$APP_ID&units=metric&mode=json")
    suspend fun getCurrentWeatherDataAPI(@Query("lat") lat: Double, @Query("lon") lon: Double): Any

    @GET("${END_POINT_PREFIX}forecast?appid=$APP_ID&units=metric&mode=json&cnt=40")
    suspend fun getWeatherForecastDataAPI(@Query("lat") lat: Double, @Query("lon") lon: Double): Any

    @GET("${END_POINT_PREFIX}weather?&appid=$APP_ID&units=metric")
    suspend fun getWeatherCityAPI(@Query("q") city: String): Any

    @GET("${END_POINT_PREFIX}forecast?&appid=$APP_ID&units=metric&cnt=40")
    suspend fun getWeatherCityForecastAPI(@Query("q") city: String): Any
}
