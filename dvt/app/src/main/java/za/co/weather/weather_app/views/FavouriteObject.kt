package za.co.weather.weather_app.views

import io.realm.RealmObject
import org.json.JSONArray
import org.json.JSONObject

open class FavouriteData : RealmObject() {

    var id = 0L
    var name: String = ""
    var coordinates: String = ""
    var weather: String = ""
    var main: String = ""

}

//var coordinates: JSONObject
//var weather: JSONArray
//
//var base: String
//var main: JSONObject
//
//var visibility: Double
//var wind: JSONObject
//var clouds: JSONObject
//
//var dt: String
//var sys: JSONObject
//
//var timeZone: Long
//var id: String
//var name: String
//var code: String