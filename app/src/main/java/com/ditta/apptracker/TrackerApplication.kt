package com.ditta.apptracker

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.ditta.apptracker.datastore.DataStoreRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.*

class TrackerApplication : Application() {

    private val dataStoreRepository: DataStoreRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@TrackerApplication)
            modules(appModule)
        }

        if (dataStoreRepository.readInstallationMillisec() == 0L) {
            dataStoreRepository.store(Calendar.getInstance().time.time)
        }
    }
}