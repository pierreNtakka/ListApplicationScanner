package com.ditta.apptracker.datastore

import com.ditta.apptracker.model.UserStatsDataModel

interface UserStatsRepository {

    fun hasPermission(): Boolean
    fun retrieveStats(start: Long, end: Long): List<UserStatsDataModel>
}