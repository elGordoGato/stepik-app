package com.ipr.stepikapp

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()
        Realm.setDefaultConfiguration(config)
        getSharedPreferences("name", 0).edit().putString("zz", "xx").apply()

        getSharedPreferences("name", 0).getString("zz", "")
    }
}