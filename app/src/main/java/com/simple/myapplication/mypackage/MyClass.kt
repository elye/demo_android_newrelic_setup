package com.simple.myapplication.mypackage

import android.util.Log
import com.newrelic.agent.android.NewRelic
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyClass {

    private val random = Random(1000)

    fun doSomething(condition: () -> Boolean, message: String) {
        val value = random.nextInt()

        GlobalScope.launch {
            delay(1000)
            Log.d("Printing", "Testing $value")
            if (random.nextInt() > 1000) {
                hiddenInside(condition, message)
            }
        }
    }

    private fun hiddenInside(condition: () -> Boolean, message: String) {
        Log.d("Printing", "Logging to new relic")
        val result = NewRelic.recordHandledException(Exception("Something beyond"), null)
        Log.d("Printing", "Handle exception $result")
        GlobalScope.launch {
            delay(1000)
            require(condition.invoke()) { message }
        }
    }
}
