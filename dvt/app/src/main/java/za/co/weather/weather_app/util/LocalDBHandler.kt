package za.co.weather.weather_app.util

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import org.json.JSONArray
import org.json.JSONObject
import za.co.weather.weather_app.model.CurrentTemperatureData
import za.co.weather.weather_app.model.FavouriteObject
import za.co.weather.weather_app.model.ForecastObject
import za.co.weather.weather_app.model.ForecastTemperatureData

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

        fun createCurrent(current: CurrentTemperatureData?, forecast: ArrayList<ForecastTemperatureData>?) {

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
                        newFav.visibility = current.visibility
                        newFav.lastUpdated = current.lastUpdated
                    }
                } else {
                    deleteForecast(current.name)
                    updateCurrent(current)
                    createForecast(forecast)
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }

        fun readCurrent(city: String = ""): Pair<CurrentTemperatureData?, ArrayList<ForecastTemperatureData>?>? {

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
                            reCity.visibility,
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


//            val fore = readForecast(temp?.name)

            return Pair(temp, readForecast(temp?.name))
        }

        private fun updateCurrent(current: CurrentTemperatureData?) {

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
                        reFaves.visibility = current.visibility
                    }

                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }

        fun deleteCurrent(city: String, country: String) {

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
                deleteForecast(city)

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }

        fun readFavourites(): ArrayList<CurrentTemperatureData> {

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





        private fun createForecast(forecast: ArrayList<ForecastTemperatureData>?) {

            if (forecast.isNullOrEmpty()) {
                return
            }
            var realm: Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFave = realm.where(ForecastObject::class.java)
                    .equalTo("name", forecast[0].city.getString("name"))
                    .findAll()

                if (reFave.isLoaded && reFave.isEmpty()) {

//                    deleteForecast(forecast[0].city.getString("name"))

                    realm.executeTransaction { rlm ->

                        forecast.forEach { d ->
                            val millis = System.currentTimeMillis()
                            val newForecast = rlm.createObject(ForecastObject::class.java, millis)

                            newForecast.name = d.city.getString("name")
                            newForecast.city = d.city.toString()

                            newForecast.date = d.date
                            newForecast.main = d.main.toString()

                            newForecast.weather = d.weather.toString()
//                            Thread.sleep(100)
                        }
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }

        private fun readForecast(city: String?): ArrayList<ForecastTemperatureData>? {

            val arr = arrayListOf<ForecastTemperatureData>()

            if (city.isNullOrEmpty()) {
                return arr
            }
            var realm: Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFave = realm.where(ForecastObject::class.java)
                    .equalTo("name", city)
                    .findAll()

                if (reFave.isLoaded && reFave.isNotEmpty()) {

                    realm.executeTransaction { rlm ->

                        reFave.forEach { d ->
                            val temp = ForecastTemperatureData(
                                "", JSONObject(d.main), JSONArray(d.weather),
                            JSONObject(), JSONObject(), 0.0, 0.0,
                                JSONObject(), JSONObject(), d.date, JSONObject(d.city))

//                            val millis = System.currentTimeMillis()
//                            val newForecast = rlm.createObject(ForecastObject::class.java, millis)
//
//                            newForecast.name = d.city.getString("name")
//                            newForecast.city = d.city.toString()
//
//                            newForecast.date = d.date
//                            newForecast.main = d.main.toString()
//
//                            newForecast.weather = d.weather.toString()
//                            Thread.sleep(100)

                            arr.add(temp)
                        }
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }

            return arr
        }

        private fun deleteForecast(city: String) {

            var realm: Realm? = null
            try {

                realm = Realm.getDefaultInstance()

                val reFaves = realm.where(ForecastObject::class.java)
                    .equalTo("name", city)
                    .findAll()

                if (reFaves.isLoaded && !reFaves.isNullOrEmpty()) {
                    realm.executeTransaction {
                        reFaves.forEach { r ->
                            r.deleteFromRealm()
                        }
                    }
                }

            } catch (e: Exception) {
                println(e.message)
            } finally {
                closeRealmInstance(realm)
            }
        }

























//        fun updateCurrent(city: String, current: CurrentTemperatureData?) {
//
//            if (city.isNullOrBlank() || current == null) {
//                return
//            }
//
//            var realm: Realm? = null
//            try {
//
//                realm = Realm.getDefaultInstance()
//
//                val reFaves = realm.where(FavouriteObject::class.java)
//                    .equalTo("name", city)
//                    .findFirst()
//
//                if (reFaves != null) {
//                    realm.executeTransaction {
//                        reFaves.name = current.name
//                        reFaves.coordinates = current.coordinates.toString()
//                        reFaves.main = current.main.toString()
//                        reFaves.weather = current.weather.toString()
//                        reFaves.sys = current.sys.toString()
//                    }
//                }
//
//            } catch (e: Exception) {
//                println(e.message)
//            } finally {
//                closeRealmInstance(realm)
//            }
//        }



//        fun deleteForecast(data: ForecastTemperatureData) {
//
//            var realm: Realm? = null
//            try {
//
//                realm = Realm.getDefaultInstance()
//
//                val reFaves = realm.where(ForecastObject::class.java)
//                    .equalTo("name", data.city.getString("name"))
//                    .findAll()
//
//                if (reFaves.isLoaded && !reFaves.isNullOrEmpty()) {
//                    realm.executeTransaction {
//                        reFaves.forEach { r ->
//                            if (JSONObject(r.city).getString("country").compareTo(data.city.getString("country")) == 0) {
//                                r.deleteFromRealm()
//                            }
//                        }
//                    }
//                }
//
//            } catch (e: Exception) {
//                println(e.message)
//            } finally {
//                closeRealmInstance(realm)
//            }
//        }











        private fun closeRealmInstance(rlm: Realm?) {
            if (rlm != null) rlm.close() else println("realm closed")
        }
    }
}
