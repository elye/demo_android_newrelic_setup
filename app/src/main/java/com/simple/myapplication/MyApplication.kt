package com.simple.myapplication

import android.app.Application
import android.os.SystemClock
import com.newrelic.agent.android.NewRelic
import com.simple.myapplication.mypackage.Foreground

class MyApplication: Application() {

    private var foregroundListener: Foreground.Listener? = null
    override fun onCreate() {
        super.onCreate()

        NewRelic.withApplicationToken(
            "<NEWRELIC-APPKEY>"
        ).start(this)

        Foreground.init(this)
        foregroundListener = object : Foreground.Listener {
            override fun onBecameForeground() {}
            override fun onBecameBackground() {}
        }
        Foreground.get().addListener(foregroundListener)
    }
}