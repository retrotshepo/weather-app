package za.co.weather.weather_app.util

class WeatherAPIEndpoint {

    companion object {

        suspend fun getWeatherCurrentCALL(latitude: Double, longitude: Double) =
            RetrofitService.getClient()?.getCurrentWeatherDataAPI(latitude, longitude)

        suspend fun getWeatherForecastCALL(latitude: Double, longitude: Double) =
            RetrofitService.getClient()?.getWeatherForecastDataAPI(latitude, longitude)
    }
}