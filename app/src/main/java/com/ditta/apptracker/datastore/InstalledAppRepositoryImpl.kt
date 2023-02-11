package com.ditta.apptracker.datastore

import android.content.Context
import android.content.pm.PackageManager

class InstalledAppRepositoryImpl(private val context: Context) : InstalledAppRepository {


    override fun getPackages(): List<String> =
        context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA).map {
            it.packageName
        }
}