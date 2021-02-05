package za.co.weather.weather_app.util

class WeatherAPIEndpoint {

    companion object {

        suspend fun getWeatherCALL(latitude: Double, longitude: Double) =
            RetrofitService.getClient()?.getWeatherDataAPI(latitude, longitude)
    }
}