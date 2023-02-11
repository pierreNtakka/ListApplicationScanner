package com.ditta.apptracker.datastore


interface InstalledAppRepository {

    fun getPackages(): List<String>
}