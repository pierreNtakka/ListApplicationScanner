package com.ditta.apptracker.datastore

import android.content.SharedPreferences
import com.ditta.apptracker.model.AppInfoUi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataStoreRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : DataStoreRepository {

    companion object {
        private const val INSTALLED_APP_KEY = "INSTALLED_APP_KEY"
        private const val APP_INSTALLATION_MILLISEC = "APP_INSTALLATION_MILLISEC"
    }

    override fun store(list: List<AppInfoUi>) {
        sharedPreferences.edit()
            .putString(INSTALLED_APP_KEY, gson.toJson(list))
            .apply()
    }

    override fun read(): List<AppInfoUi> {
        val read = sharedPreferences.getString(INSTALLED_APP_KEY, "")

        return if (read?.isNotEmpty() == true) {
            val listOfMyClassObject = object : TypeToken<ArrayList<AppInfoUi?>?>() {}.type

            gson.fromJson(read, listOfMyClassObject)
        } else {
            emptyList()
        }
    }

    override fun store(appInstallationMillisec: Long) {
        sharedPreferences.edit()
            .putLong(APP_INSTALLATION_MILLISEC, appInstallationMillisec)
            .apply()
    }

    override fun readInstallationMillisec(): Long {
        return sharedPreferences.getLong(APP_INSTALLATION_MILLISEC, 0)
    }

}