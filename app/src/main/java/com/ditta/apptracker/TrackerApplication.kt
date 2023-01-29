package com.ditta.apptracker

import android.app.Application
import com.ditta.apptracker.datastore.SharedPrefRepository
import java.util.*

class TrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPrefRepository = SharedPrefRepository(this)

        if (sharedPrefRepository.readInstallationMillisec() == 0L) {
            sharedPrefRepository.store(Calendar.getInstance().time.time)
        }
    }
}