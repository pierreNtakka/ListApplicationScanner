package com.ditta.apptracker

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ditta.apptracker.datastore.DataStoreRepository
import com.ditta.apptracker.datastore.DataStoreRepositoryImpl
import com.ditta.apptracker.datastore.UserStatistics
import com.ditta.apptracker.datastore.UserStatisticsImpl
import com.ditta.apptracker.viewmodel.AppInfoViewModel
import com.ditta.tracker.RetrieverAndroidAppInfo
import com.ditta.tracker.communication.InstalledAppRepository
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val koinQualifierGson = named("GSON")

val repositoryModule = module {
    single<Gson>(koinQualifierGson) {
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("dd-MM-yyyy HH:mm:ss")
            .create()
    }

    single {
        getSharedPrefs(androidApplication())
    }

    single<DataStoreRepository> {
        DataStoreRepositoryImpl(get(), get(qualifier = koinQualifierGson))
    }

    single<InstalledAppRepository> {
        RetrieverAndroidAppInfo(androidApplication())
    }

    single<UserStatistics> {
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