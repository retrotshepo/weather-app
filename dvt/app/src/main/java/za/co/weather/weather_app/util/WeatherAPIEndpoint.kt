package za.co.weather.weather_app.util

class WeatherAPIEndpoint {

    companion object {

        suspend fun getWeatherCALL() =
            RetrofitService.getClient()?.getWeatherDataAPI()
    }
}