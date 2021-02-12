package za.co.weather.weather_app.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.json.JSONArray
import org.json.JSONObject

@RealmClass
open class FavouriteObject : RealmObject() {

    @PrimaryKey
    var id = 0L
    var name: String = ""
    var coordinates: String = ""
    var weather: String = ""
    var main: String = ""
    var sys: String = ""
    var visibility: Double = 0.0
    var lastUpdated: Long = 0L

}
