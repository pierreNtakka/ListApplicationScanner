package com.ditta.tracker

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.ditta.tracker.communication.InstalledAppInterface
import com.ditta.tracker.model.AppInfo

class RetrieverAndroidAppInfo(private val context: Context) : InstalledAppInterface {


    override fun getApplicationInfo(): List<AppInfo> {
        val applicationInfo: List<ApplicationInfo> =
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return applicationInfo.map {
            AppInfo(it.uid,it.packageName)
        }.toList()
    }
}