package com.simple.myapplication.mypackage

import android.util.Log
import com.newrelic.agent.android.NewRelic
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyClass {

    private val random = Random(1000)

    fun doSomething(crashOut: Boolean, message: String = "Testing NewRelic") {
        val value = random.nextInt()

        GlobalScope.launch {
            delay(1000)
            if (random.nextInt() > 1000) {
                hiddenInside(crashOut, message)
            }
        }
    }

    private fun hiddenInside(crashOut: Boolean, message: String) {
        GlobalScope.launch {
            Log.d("Tracking", "NewRelic Test Action: $crashOut")
            delay(1000)
            if (crashOut) {
                throw Exception(message)
            } else {
                NewRelic.recordHandledException(Exception(message), null)
            }
        }
    }
}
