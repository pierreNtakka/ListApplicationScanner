package com.ditta.tracker.communication

import com.ditta.tracker.model.AppInfo


interface InstalledAppRepository {

    fun getApplicationInfo(): List<AppInfo>
}