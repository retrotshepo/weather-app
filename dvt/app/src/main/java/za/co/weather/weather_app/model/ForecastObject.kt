package za.co.weather.weather_app.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class ForecastObject: RealmObject() {

    @PrimaryKey
    var id = 0L
    var name: String = ""

    var main: String = ""
    var weather: String = ""

    var date: String = ""
    var city: String = ""
}
