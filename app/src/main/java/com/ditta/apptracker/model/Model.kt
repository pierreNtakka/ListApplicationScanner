package com.ditta.apptracker.model

import com.ditta.apptracker.utils.DateUtils
import java.util.*

class AppInfoUi(
    val packageName: String,
    var toTrack: Boolean,
    var isInstalled: Boolean? = true,
    var startDate: Date? = null,
    var endDate: Date? = null
) {

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
}


data class UserStatsDataModel(
    val packageName: String,
    val firstTimeStamp: Long,
    val lastTimeStamp: Long
)
