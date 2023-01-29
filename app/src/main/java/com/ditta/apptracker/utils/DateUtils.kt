package com.ditta.apptracker.utils

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    val DATE_FORMAT_ITALIAN_OUTPUT = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALIAN)

    private val TAG: String = DateUtils::class.java.name

    fun formatDate(dateIn: Date?, formatOut: DateFormat): String? {
        dateIn?.let {
            try {
                return formatOut.format(dateIn)
            } catch (e: Exception) {
                Log.e(
                    TAG, e.message, e
                )
            }
        }
        return null
    }
}