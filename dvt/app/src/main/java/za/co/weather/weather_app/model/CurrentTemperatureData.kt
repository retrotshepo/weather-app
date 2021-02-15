package za.co.weather.weather_app.model

import org.json.JSONArray
import org.json.JSONObject

class CurrentTemperatureData {

    var coordinates: JSONObject
    var weather: JSONArray

    var base: String
    var main: JSONObject

    var visibility: Double
    var wind: JSONObject
    var clouds: JSONObject

    var dt: String
    var sys: JSONObject

    var timeZone: Long
    var id: String
    var name: String
    var code: String
    var lastUpdated: Long


    constructor(
        coordinates: JSONObject,
        weather: JSONArray,
        base: String,
        main: JSONObject,
        visibility: Double,
        wind: JSONObject,
        clouds: JSONObject,
        dt: String,
        sys: JSONObject,
        timeZone: Long,
        id: String,
        name: String,
        code: String,
        lastUpdated: Long = 0L
    ) {
        this.coordinates = coordinates
        this.weather = weather
        this.base = base
        this.main = main
        this.visibility = visibility
        this.wind = wind
        this.clouds = clouds
        this.dt = dt
        this.sys = sys
        this.timeZone = timeZone
        this.id = id
        this.name = name
        this.code = code
        this.lastUpdated = lastUpdated
    }
}
