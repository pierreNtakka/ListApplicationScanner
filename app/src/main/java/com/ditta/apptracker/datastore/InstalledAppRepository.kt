package com.ditta.apptracker.datastore


interface InstalledAppRepository {

    fun getApplicationInfo(): List<String>
}