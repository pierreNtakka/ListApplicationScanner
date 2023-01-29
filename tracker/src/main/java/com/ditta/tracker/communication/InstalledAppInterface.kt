package com.ditta.tracker.communication

import com.ditta.tracker.model.AppInfo


interface InstalledAppInterface {

    fun getApplicationInfo(): List<AppInfo>
}