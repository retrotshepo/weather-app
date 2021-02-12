package za.co.weather.weather_app.util

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import org.json.JSONArray
import org.json.JSONObject
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.model.FavouriteObject

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

            if (current == null) {
                return
            }
            var realm: Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFave = realm.where(FavouriteObject::class.java)
                    .equalTo("name", current.name)
                    .findFirst()

                if (reFave == null) {
                    val millis = System.currentTimeMillis()
                    realm.executeTransaction { rlm ->
                        val newFav = rlm.createObject(FavouriteObject::class.java, millis)
                        newFav.name = current.name
                        newFav.coordinates = current.coordinates.toString()
                        newFav.weather = current.weather.toString()
                        newFav.main = current.main.toString()
                        newFav.sys = current.sys.toString()
                    }
                } else {
                    update(current)
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }

        fun read(): ArrayList<CurrentTemperatureData> {

            val array = arrayListOf<CurrentTemperatureData>()
            var realm: Realm? = null

            try {

                realm = Realm.getDefaultInstance()

                val reCities = realm.where(FavouriteObject::class.java)
                    .findAll()

                if (reCities.isLoaded) {

                    reCities.forEach { c ->
                        val nCurrentTemperatureData =
                            CurrentTemperatureData(
                                JSONObject(c.coordinates),
                                JSONArray(c.weather),
                                "",
                                JSONObject(c.main),
                                0.0,
                                JSONObject(),
                                JSONObject(),
                                "",
                                JSONObject(c.sys),
                                0L,
                                "",
                                c.name,
                                ""
                            )

                        array.add(nCurrentTemperatureData)
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
//                closeRealmInstance(realm)
            }
            return array
        }

        fun read(city: String): CurrentTemperatureData? {

            var temp: CurrentTemperatureData? = null
            var realm: Realm? = null

            try {

                realm = Realm.getDefaultInstance()

                var reCity: FavouriteObject? = null
                reCity = when (city.isBlank()) {

                    true -> {
                        realm.where(FavouriteObject::class.java)
                            .findFirst()
                    }
                    false -> {
                        realm.where(FavouriteObject::class.java)
                            .equalTo("name", city)
                            .findFirst()
                    }
                }

                if (reCity != null) {

                    val nCurrentTemperatureData =
                        CurrentTemperatureData(
                            JSONObject(reCity.coordinates),
                            JSONArray(reCity.weather),
                            "",
                            JSONObject(reCity.main),
                            0.0,
                            JSONObject(),
                            JSONObject(),
                            "",
                            JSONObject(reCity.sys),
                            0L,
                            "",
                            reCity.name,
                            "", reCity.lastUpdated
                        )

                    temp = nCurrentTemperatureData
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
            return temp
        }

        fun update(current: CurrentTemperatureData?) {

            if (current == null) {
                return
            }

            var realm: Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFaves = realm.where(FavouriteObject::class.java)
                    .equalTo("name", current.name)
                    .findFirst()

                if (reFaves != null) {
                    realm.executeTransaction {
                        reFaves.name = current.name
                        reFaves.coordinates = current.coordinates.toString()
                        reFaves.main = current.main.toString()
                        reFaves.weather = current.weather.toString()
                        reFaves.sys = current.sys.toString()
                        reFaves.lastUpdated = current.lastUpdated
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }


        fun update(city: String, current: CurrentTemperatureData?) {

            if (city.isNullOrBlank() || current == null) {
                return
            }

            var realm: Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFaves = realm.where(FavouriteObject::class.java)
                    .equalTo("name", city)
                    .findFirst()

                if (reFaves != null) {
                    realm.executeTransaction {
                        reFaves.name = current.name
                        reFaves.coordinates = current.coordinates.toString()
                        reFaves.main = current.main.toString()
                        reFaves.weather = current.weather.toString()
                        reFaves.sys = current.sys.toString()
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }



        fun delete(city: String, country: String) {

            var realm: Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFaves = realm.where(FavouriteObject::class.java)
                    .equalTo("name", city)
                    .findAll()

                if (reFaves.isLoaded && !reFaves.isNullOrEmpty()) {
                    realm.executeTransaction {
                        reFaves.forEach { r ->
                            if (JSONObject(r.sys).getString("country").compareTo(country) == 0) {
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
            if (rlm != null) rlm.close() else println("realm closed")
        }
    }
}
