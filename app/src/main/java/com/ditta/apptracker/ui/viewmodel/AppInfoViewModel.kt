package com.ditta.apptracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ditta.apptracker.datastore.DataStoreRepository
import com.ditta.apptracker.datastore.InstalledAppRepository
import com.ditta.apptracker.datastore.UserStatsRepository
import com.ditta.apptracker.model.AppInfoUi
import java.util.*

class AppInfoViewModel(
    private val appInstalledRepository: InstalledAppRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val userStatistics: UserStatsRepository,
) : ViewModel() {

    private val _appInfo = MutableLiveData<List<AppInfoUi>>()
    val appInfo: LiveData<List<AppInfoUi>> = _appInfo

    private val _requestPermissionPermission = MutableLiveData(false)
    val requestPermission: LiveData<Boolean> = _requestPermissionPermission

    fun updateUI() {
        val listRetrievedFromDevice = appInstalledRepository.getPackages()
        val listRetrievedFromDatastore = dataStoreRepository.read().toMutableList()

        val difference = listRetrievedFromDatastore.filterNot { appInfo ->

            val element = listRetrievedFromDevice.firstOrNull {
                it == appInfo.packageName
            }
            element != null
        }

        if (difference.isNotEmpty()) {
            listRetrievedFromDatastore.map { appInfoUi ->

                val element = listRetrievedFromDevice.firstOrNull {
                    it == appInfoUi.packageName
                }

                if (element == null) {
                    appInfoUi.isInstalled = false
                    appInfoUi.toTrack = false
                }
            }
        } else {
            listRetrievedFromDevice.forEach {
                listRetrievedFromDatastore.add(AppInfoUi(packageName = it))
            }
        }


        val listToTrack = listRetrievedFromDatastore.filter { it.toTrack }

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

        _appInfo.value = listRetrievedFromDatastore
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

        dataStoreRepository.store(apps = _appInfo.value as ArrayList)
    }


    fun storeAppInfo() {
        _appInfo.value?.let {
            dataStoreRepository.store(apps = it)
        }
    }

    fun checkPermission() {
        if (!userStatistics.hasPermission()) {
            _requestPermissionPermission.value = true
        }
    }
}