package com.ditta.apptracker.model

import org.junit.Assert.*
import org.junit.Test

class AppInfoUiUnitTest {

    @Test
    fun toJson() {
        val expectedJson =
            "{\"package_name\":\"com.ditta.apptracker\",\"to_track\":true,\"is_installed\":true}"
        val jsonString = AppInfoUi("com.ditta.apptracker", true).toJson()
        assertEquals(expectedJson, jsonString)
    }

    @Test
    fun fromJson() {
        val sourceJson =
            "{\"package_name\":\"com.ditta.apptracker\",\"to_track\":true,\"is_installed\":true}"

        val appInfoUi = AppInfoUi.fromJson(sourceJson)
        assertEquals(appInfoUi.packageName, "com.ditta.apptracker")
        assertTrue(appInfoUi.toTrack)

        if (appInfoUi.isInstalled == null) {
            fail()
        } else {
            assertTrue(appInfoUi.isInstalled!!)
        }
    }
}