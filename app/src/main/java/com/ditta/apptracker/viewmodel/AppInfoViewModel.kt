package com.ditta.apptracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ditta.apptracker.datastore.InfoStatsManager
import com.ditta.apptracker.datastore.SharedPrefRepository
import com.ditta.apptracker.model.AppInfoUi
import com.ditta.tracker.communication.InstalledAppInterface
import java.util.*

class AppInfoViewModel(
    private val appInstalledRepository: InstalledAppInterface,
    private val sharedPrefRepository: SharedPrefRepository,
    private val infoStatsManager: InfoStatsManager,
) : ViewModel() {

    private val _appInfo = MutableLiveData<List<AppInfoUi>>()
    val appInfo: LiveData<List<AppInfoUi>> = _appInfo

    private val _requestPermissionPermission = MutableLiveData(false)
    val requestPermission: LiveData<Boolean> = _requestPermissionPermission

    fun updateUI() {
        val listRetrievedFromDevice = appInstalledRepository.getApplicationInfo()
        val listRetrievedFromDatastore = sharedPrefRepository.read()
        val list = listRetrievedFromDatastore as MutableList<AppInfoUi>

        if (listRetrievedFromDatastore.isEmpty()) {

            listRetrievedFromDevice.forEach {
                list.add(AppInfoUi(it.packageName, false))
            }

        } else if (listRetrievedFromDatastore.size > listRetrievedFromDevice.size) {

            list.forEach { appInfoUI ->

                val element = listRetrievedFromDevice.firstOrNull {
                    it.packageName == appInfoUI.packageName
                }

                if (element == null) {
                    appInfoUI.isInstalled = false
                    appInfoUI.toTrack = false
                }
            }

        } else {
            listRetrievedFromDevice.forEach { appInfo ->
                val element = listRetrievedFromDatastore.firstOrNull {
                    it.packageName == appInfo.packageName
                }

                if (element == null) {
                    list.add(AppInfoUi(appInfo.packageName, false))
                }
            }
        }

        val listToTrack = list.filter { it.toTrack }

        if (listToTrack.isNotEmpty()) {

            val usageStats = infoStatsManager.retrieveStats(
                sharedPrefRepository.readInstallationMillisec(),
                System.currentTimeMillis()
            )

            listToTrack.forEach { app ->
                usageStats.firstOrNull {
                    it.packageName == app.packageName
                }?.let {
                    if (it.firstTimeStamp > 0) {
                        app.startDate = Date(it.firstTimeStamp)
                    }
                    if (it.lastTimeUsed > 0) {
                        app.endDate = Date(it.lastTimeUsed)
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

        sharedPrefRepository.store(_appInfo.value as ArrayList)
    }


    fun storeAppInfo() {
        _appInfo.value?.let {
            sharedPrefRepository.store(it)
        }
    }

    fun checkPermission() {
        if (!infoStatsManager.hasPermission()) {
            _requestPermissionPermission.value = true
        }
    }

}

class AppInfoViewModelFactory(
    private val appInstalledRepository: InstalledAppInterface,
    private val sharedPrefRepository: SharedPrefRepository,
    private val infoStatsManager: InfoStatsManager
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppInfoViewModel(
                appInstalledRepository,
                sharedPrefRepository,
                infoStatsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}