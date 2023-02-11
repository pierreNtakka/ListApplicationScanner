package com.ditta.apptracker.model

import com.ditta.apptracker.appModule
import com.ditta.apptracker.gsonKoinQualifierName
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class AppInfoUiUnitTest : KoinTest {

    private val gson by inject<Gson>(qualifier = gsonKoinQualifierName)

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(appModule)
    }
    
    @Test
    fun toJson() {
        val expectedJson =
            "{\"package_name\":\"com.ditta.apptracker\",\"to_track\":true,\"is_installed\":true}"
        val jsonString = gson.toJson(AppInfoUi("com.ditta.apptracker", true))
        assertEquals(expectedJson, jsonString)
    }

    @Test
    fun fromJson() {
        val sourceJson =
            "{\"package_name\":\"com.ditta.apptracker\",\"to_track\":true,\"is_installed\":true}"

        val appInfoUi = gson.fromJson(sourceJson, AppInfoUi::class.java)
        assertEquals(appInfoUi.packageName, "com.ditta.apptracker")
        assertTrue(appInfoUi.toTrack)

        if (appInfoUi.isInstalled == null) {
            fail()
        } else {
            assertTrue(appInfoUi.isInstalled!!)
        }
    }
}