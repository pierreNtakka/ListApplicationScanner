package com.ditta.apptracker.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ditta.apptracker.model.AppInfoUi
import com.google.gson.reflect.TypeToken


class SharedPrefRepository(context: Context) {

    companion object {
        private const val INSTALLED_APP_KEY = "INSTALLED_APP_KEY"
        private const val APP_INSTALLATION_MILLISEC = "APP_INSTALLATION_MILLISEC"
    }

    private val sharedPreferences: SharedPreferences

    init {
        val masterKey: MasterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    fun store(list: List<AppInfoUi>) {
        sharedPreferences.edit()
            .putString(INSTALLED_APP_KEY, GsonProvider.gson.toJson(list))
            .apply()
    }

    fun read(): List<AppInfoUi> {
        val read = sharedPreferences.getString(INSTALLED_APP_KEY, "")

        return if (read?.isNotEmpty() == true) {
            val listOfMyClassObject = object : TypeToken<ArrayList<AppInfoUi?>?>() {}.type

            GsonProvider.gson.fromJson(read, listOfMyClassObject)
        } else {
            ArrayList()
        }
    }

    fun store(appInstallationMillisec: Long) {
        sharedPreferences.edit()
            .putLong(APP_INSTALLATION_MILLISEC, appInstallationMillisec)
            .apply()
    }

    fun readInstallationMillisec(): Long {
        return sharedPreferences.getLong(APP_INSTALLATION_MILLISEC, 0)
    }

}