package com.ditta.apptracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ditta.apptracker.datastore.DataStoreRepository
import com.ditta.apptracker.datastore.InstalledAppRepository
import com.ditta.apptracker.datastore.UserStatistics
import com.ditta.apptracker.model.AppInfoUi
import java.util.*

class AppInfoViewModel(
    private val appInstalledRepository: InstalledAppRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val userStatistics: UserStatistics,
) : ViewModel() {

    private val _appInfo = MutableLiveData<List<AppInfoUi>>()
    val appInfo: LiveData<List<AppInfoUi>> = _appInfo

    private val _requestPermissionPermission = MutableLiveData(false)
    val requestPermission: LiveData<Boolean> = _requestPermissionPermission

    fun updateUI() {
        val listRetrievedFromDevice = appInstalledRepository.getPackages()
        val listRetrievedFromDatastore = dataStoreRepository.read()
        val list = listRetrievedFromDatastore.toMutableList()

        if (listRetrievedFromDatastore.isEmpty()) {

            listRetrievedFromDevice.forEach {
                list.add(AppInfoUi(it, false))
            }

        } else if (listRetrievedFromDatastore.size > listRetrievedFromDevice.size) {

            list.forEach { appInfoUI ->

                val element = listRetrievedFromDevice.firstOrNull {
                    it == appInfoUI.packageName
                }

                if (element == null) {
                    appInfoUI.isInstalled = false
                    appInfoUI.toTrack = false
                }
            }

        } else {
            listRetrievedFromDevice.forEach { appInfo ->
                val element = listRetrievedFromDatastore.firstOrNull {
                    it.packageName == appInfo
                }

                if (element == null) {
                    list.add(AppInfoUi(appInfo, false))
                }
            }
        }

        val listToTrack = list.filter { it.toTrack }

        if (listToTrack.isNotEmpty()) {

            val usageStats = userStatistics.retrieveStats(
                dataStoreRepository.readInstallationMillisec(),
                System.currentTimeMillis()
            )

            listToTrack.forEach { app ->
                usageStats.firstOrNull {
                    it.packageName == app.packageName
                }?.let {
                    if (it.firstTimeStamp > 0) {
                        app.startDate = Date(it.firstTimeStamp)
                    }
                    if (it.lastTimeStamp > 0) {
                        app.endDate = Date(it.lastTimeStamp)
                    }
                }
            }

        }

        _appInfo.value = list
    }

    fun clearAllSelection() {
        _appInfo.value?.forEach {
            it.toTrack = false
        }
        _appInfo.value = _appInfo.value
    }

    fun onItemAppChecked(appInfoUI: AppInfoUi) {
        _appInfo.value?.find { it.packageName == appInfoUI.packageName }?.toTrack =
            appInfoUI.toTrack

        dataStoreRepository.store(_appInfo.value as ArrayList)
    }


    fun storeAppInfo() {
        _appInfo.value?.let {
            dataStoreRepository.store(it)
        }
    }

    fun checkPermission() {
        if (!userStatistics.hasPermission()) {
            _requestPermissionPermission.value = true
        }
    }
}