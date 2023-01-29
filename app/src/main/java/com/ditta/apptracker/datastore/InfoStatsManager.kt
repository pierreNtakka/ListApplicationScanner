package com.ditta.apptracker.datastore

import android.app.Activity
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*

class InfoStatsManager(activity: Activity) {

    private val mUsageStatsManager: UsageStatsManager = activity
        .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager


    fun hasPermission(): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val start: Long = calendar.timeInMillis
        return retrieveStats(start, System.currentTimeMillis()).isNotEmpty()
    }


    fun retrieveStats(start: Long, end: Long): List<UsageStats> =
        mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end)


}