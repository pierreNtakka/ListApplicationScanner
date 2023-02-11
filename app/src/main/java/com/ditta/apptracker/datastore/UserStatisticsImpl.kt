package com.ditta.apptracker.datastore

import android.app.usage.UsageStatsManager
import android.content.Context
import com.ditta.apptracker.model.UserStatsDataModel
import java.util.*

class UserStatisticsImpl(context: Context) : UserStatistics {

    private val mUsageStatsManager: UsageStatsManager = context
        .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager


    override fun hasPermission(): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val start: Long = calendar.timeInMillis
        return retrieveStats(start, System.currentTimeMillis()).isNotEmpty()
    }


    override fun retrieveStats(start: Long, end: Long): List<UserStatsDataModel> {
        return mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end)
            .map {
                UserStatsDataModel(it.packageName, it.firstTimeStamp, it.lastTimeUsed)
            }
    }


}