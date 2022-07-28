package com.example.couroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class TestSuspendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_suspend)


        GlobalScope.launch {
            val time = measureTimeMillis {
                val one = sampleOne()
                val two = sampleTwo()
                println("the answer is ${one + two}")
            }
            println("Completed in $time ms")
        }

    }

    private suspend fun sampleOne(): Int {
        println("sampleOne" + System.currentTimeMillis())
        delay(1000L)
        return 10
    }

    private suspend fun sampleTwo(): Int {
        println("sampleTwo" + System.currentTimeMillis())
        delay(1000L)
        return 10
    }
}