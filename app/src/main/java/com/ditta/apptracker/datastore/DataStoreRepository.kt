package com.ditta.apptracker.datastore

import com.ditta.apptracker.model.AppInfoUi

interface DataStoreRepository {

    fun store(apps: List<AppInfoUi>)
    fun read(): List<AppInfoUi>
    fun store(appInstallationMillisec: Long)
    fun readInstallationMillisec(): Long
}