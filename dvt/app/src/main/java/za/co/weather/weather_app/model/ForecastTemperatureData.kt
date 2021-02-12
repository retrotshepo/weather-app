package za.co.weather.weather_app.model

import org.json.JSONArray
import org.json.JSONObject

class ForecastTemperatureData {

    var dt: String
    var main: JSONObject
    var weather: JSONArray
    var clouds: JSONObject
    var wind: JSONObject
    var visibility: Double

    var pop: Double
    var rain: JSONObject
    var sys: JSONObject

    var date: String

    constructor(dt: String, main: JSONObject, weather: JSONArray, clouds: JSONObject,
        wind: JSONObject, visibility: Double, pop: Double, rain: JSONObject,
        sys: JSONObject, date: String
    ) {
        this.dt = dt
        this.main = main
        this.weather = weather
        this.clouds = clouds
        this.wind = wind
        this.visibility = visibility
        this.pop = pop
        this.rain = rain
        this.sys = sys
        this.date = date
    }
}
