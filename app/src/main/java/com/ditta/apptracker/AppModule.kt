package com.ditta.apptracker

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ditta.apptracker.datastore.*
import com.ditta.apptracker.ui.viewmodel.AppInfoViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val gsonKoinQualifierName = named("GSON")
val encryptedSharedPrefKoinQualifierName = named("ENCRYPTED_SHARED_PREF")

val repositoryModule = module {
    single<Gson>(gsonKoinQualifierName) {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("dd-MM-yyyy HH:mm:ss").create()
    }

    single(encryptedSharedPrefKoinQualifierName) {
        getSharedPrefs(androidApplication())
    }

    single<DataStoreRepository> {
        DataStoreRepositoryImpl(
            get(qualifier = encryptedSharedPrefKoinQualifierName),
            get(qualifier = gsonKoinQualifierName)
        )
    }

    single<InstalledAppRepository> {
        InstalledAppRepositoryImpl(androidApplication())
    }

    single<UserStatsRepository> {
        UserStatisticsImpl(androidContext())
    }
}

val viewModelModule = module {

    viewModel {
        AppInfoViewModel(get(), get(), get())
    }
}

val appModule = module {

    includes(viewModelModule, repositoryModule)
}


fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    val masterKey: MasterKey =
        MasterKey.Builder(androidApplication).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    return EncryptedSharedPreferences.create(
        androidApplication,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}