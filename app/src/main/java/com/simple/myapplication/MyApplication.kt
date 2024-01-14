package com.simple.myapplication

import android.app.Application
import com.newrelic.agent.android.NewRelic

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        NewRelic.withApplicationToken(
            "<NEWRELIC_APP_KEY>"
        ).start(this)

    }
}