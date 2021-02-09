package za.co.weather.weather_app.util

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import org.json.JSONArray
import org.json.JSONObject
import za.co.weather.weather_app.views.CurrentTemperatureData
import za.co.weather.weather_app.views.FavouriteObject

class LocalDBHandler {

    companion object {
        fun initialize(dbName: String, context: Context): Realm {
            Realm.init(context)

            val config = RealmConfiguration.Builder()
                .name(dbName)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()

            Realm.setDefaultConfiguration(config)
            return Realm.getInstance(config)
        }

        fun create(current: CurrentTemperatureData?) {

            if(current == null) {
                return
            }
            var realm : Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFave = realm.where(FavouriteObject::class.java)
                    .equalTo("name", current.name)
//                    .equalTo("sys", current.sys.toString())
                    .findFirst()

//                val reFavess = realm.where(FavouriteObject::class.java).findAll()

                if (reFave == null) {
                    val millis = System.currentTimeMillis()
                    realm.executeTransaction {rlm ->
                        val newFav = rlm.createObject(FavouriteObject::class.java, millis)
                        newFav.name = current.name
                        newFav.coordinates = current.coordinates.toString()
                        newFav.weather = current.weather.toString()
                        newFav.main = current.main.toString()
                        newFav.sys = current.sys.toString()
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }

        }

        fun read(): ArrayList<CurrentTemperatureData> {

            var array= arrayListOf<CurrentTemperatureData>()

            var realm: Realm? = null

            try {

                realm = Realm.getDefaultInstance()

                val reCities = realm.where(FavouriteObject::class.java)
                    .findAll()

                if(reCities.isLoaded) {

                    reCities.forEach { c ->
                        val nCurrentTemperatureData = CurrentTemperatureData(JSONObject(c.coordinates),
                            JSONArray(c.weather), "", JSONObject(c.main), 0.0,JSONObject(), JSONObject(),
                            "",JSONObject(c.sys), 0L, "", c.name, "")

                        array.add(nCurrentTemperatureData)
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
            return array
        }

        fun delete(city: String, country: String) {

                var realm : Realm? = null
                try {

                    realm = Realm.getDefaultInstance()

                    val reFaves = realm.where(FavouriteObject::class.java)
                        .equalTo("name", city)
                        .findAll()

                    if (reFaves.isLoaded && !reFaves.isNullOrEmpty()) {
                        realm.executeTransaction {
                            reFaves.forEach { r ->
                                if(JSONObject(r.sys).getString("country").compareTo(country) == 0) {
                                    r.deleteFromRealm()
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    println(e.message)
                } finally {
                    closeRealmInstance(realm)
                }

        }

        private fun closeRealmInstance(rlm: Realm?) {
            if(rlm != null) rlm.close() else println("realm closed")
        }
    }
}