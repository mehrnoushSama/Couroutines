package com.example.couroutines

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.couroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // launch :
    //launch is a coroutine builder. It launches a new coroutine concurrently with the rest of the code, which continues
    // to work independently. That's why Hello has been printed first.
    //delay :
    //delay is a special suspending function. It suspends the coroutine for a specific time. Suspending a coroutine does
    // not block the underlying thread, but allows other coroutines to run and use the underlying thread for their code.
    //runBlocking:
    // runBlocking is also a coroutine builder that bridges the non-coroutine world of a regular fun main() and the code
    // with coroutines inside of runBlocking { ... } curly braces. This is highlighted in an IDE by this: CoroutineScope
    // hint right after the runBlocking opening curly brace.
    //withContext :
    //withContext():Basically, withContext is allowed us to easily change context. It means It easily switches between dispatchers.

    //different:
    //runBlocking and coroutineScope builders may look similar because they both wait for their body and all
    // its children to complete. The main difference is that the runBlocking method blocks the current thread
    // for waiting, while coroutineScope just suspends, releasing the underlying thread for other usages.
    // Because of that difference, runBlocking is a regular function and coroutineScope is a suspending function.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            setNewResult("clicked!")
            //IO , Main , Default
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }


        runBlocking {
            launch(Dispatchers.Default) {
                Log.d(TAG, "First context: $coroutineContext")
                // default context
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "Second context: $coroutineContext")
                    // IO context
                }
                Log.d(TAG, "Third context: $coroutineContext")
                // back to default context
            }
        }
    }

    private fun setNewResult(input: String) {
        binding.text.text = binding.text.text.toString() + "\n$input"
    }


    //wait
    private suspend fun fakeApiRequest() {
        //for handle timeout
        withContext(IO) {
            val job = withTimeoutOrNull(15000L) {
                val result1 = getResult1FromApi()//wait
                setTextOnMainThread(result1)

                // chon in method dar coroutine seda zade shode sabr mikone ta natije avali biad bad badi ro seda mizane
                val result2 = getResult2FromApi(result1)//wait
                setTextOnMainThread(result2)
            } // wait

            if (job == null) {
                val cancelMessage = "Cancelling job.. job took longer than 1900 ms"
                setTextOnMainThread(cancelMessage)
            }


            val job2 = launch {
                setTextOnMainThread("Result 3")
            }
        }
    }


    //paralle
//    private fun fakeApiRequest() {
//        CoroutineScope(IO).launch {
//            val job1 = launch {
//                val result1 = getResult1FromApi()
//                setTextOnMainThread(result1)
//            }
//
//            val job2 = launch {
//                val result2 = getResult2FromApi("RESULT 1")
//                setTextOnMainThread(result2)
//            }
//        }
//    }


    private suspend fun setTextOnMainThread(input: String) {

        withContext(Main)
        {
            setNewResult(input)
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        logThread("getResult1FromApi")
        return "RESULT 1"
    }

    private suspend fun getResult2FromApi(result1: String): String {
        delay(1000)
        logThread("getResult2FromApi")
        return "RESULT 2*$result1"
    }

    private fun logThread(methodName: String) {
        println("debug : $methodName : ${Thread.currentThread().name}")
    }
}