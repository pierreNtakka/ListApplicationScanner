package com.ditta.apptracker.model

import com.ditta.apptracker.datastore.GsonProvider
import com.ditta.apptracker.utils.DateUtils
import java.util.*

data class AppInfoUi(
    val packageName: String,
    var toTrack: Boolean,
    var isInstalled: Boolean? = true,
    var startDate: Date? = null,
    var endDate: Date? = null
) {

    companion object {
        fun fromJson(json: String): AppInfoUi =
            GsonProvider.gson.fromJson(json, AppInfoUi::class.java)
    }

    val startDateAsString: String
        get() {
            return startDate?.let {
                DateUtils.formatDate(startDate, DateUtils.DATE_FORMAT_ITALIAN_OUTPUT)
            } ?: ""
        }

    val endDateAsString: String
        get() {
            return endDate?.let {
                DateUtils.formatDate(endDate, DateUtils.DATE_FORMAT_ITALIAN_OUTPUT)
            } ?: ""
        }

    fun toJson(): String = GsonProvider.gson.toJson(this)
}


data class UserStatsDataModel(
    val packageName: String,
    val firstTimeStamp: Long,
    val lastTimeStamp: Long
)