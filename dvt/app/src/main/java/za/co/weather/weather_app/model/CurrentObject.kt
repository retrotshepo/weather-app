package za.co.weather.weather_app.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class CurrentObject : RealmObject() {

    @PrimaryKey
    var id = 0L
    var name: String = ""
    var coordinates: String = ""
    var weather: String = ""
    var main: String = ""
    var sys: String = ""
}