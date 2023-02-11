package com.ditta.apptracker.datastore

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

class InstalledAppRepositoryImpl(private val context: Context) : InstalledAppRepository {


    override fun getPackages(): List<String> {
        val applicationInfo: List<PackageInfo> =
            context.packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

        return applicationInfo.map {
            it.packageName
        }
    }
}