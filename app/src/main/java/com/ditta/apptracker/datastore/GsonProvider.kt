package com.ditta.apptracker.datastore

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonProvider {

    val gson: Gson by lazy {
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("dd-MM-yyyy HH:mm:ss")
            .create()
    }
}