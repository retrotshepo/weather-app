package za.co.weather.weather_app.retrofit

class WeatherAPIEndpoint {

    companion object {

        suspend fun getWeatherCurrentCALL(latitude: Double, longitude: Double) =
            RetrofitService.getClient()?.getCurrentWeatherDataAPI(latitude, longitude)

        suspend fun getWeatherForecastCALL(latitude: Double, longitude: Double) =
            RetrofitService.getClient()?.getWeatherForecastDataAPI(latitude, longitude)

        suspend fun getWeatherCityCALL(city: String) =
            RetrofitService.getClient()?.getWeatherCityAPI(city)

        suspend fun getWeatherCityForecastCALL(city: String) =
            RetrofitService.getClient()?.getWeatherCityForecastAPI(city)
    }
}