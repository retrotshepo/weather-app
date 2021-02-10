package za.co.weather.weather_app.util

import za.co.weather.weather_app.retrofit.RetrofitService

class WeatherAPIEndpoint {

    companion object {

        suspend fun getWeatherCurrentCALL(latitude: Double, longitude: Double) =
            RetrofitService.getClient()?.getCurrentWeatherDataAPI(latitude, longitude)

        suspend fun getWeatherForecastCALL(latitude: Double, longitude: Double) =
            RetrofitService.getClient()?.getWeatherForecastDataAPI(latitude, longitude)

        suspend fun getWeatherCityCALL(city: String = "Soweto") =
            RetrofitService.getClient()?.getWeatherCityAPI(city)

        suspend fun getWeatherCityForecastCALL(city: String = "Soweto") =
            RetrofitService.getClient()?.getWeatherCityForecastAPI(city)
    }
}