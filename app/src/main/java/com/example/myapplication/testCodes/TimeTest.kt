package com.example.myapplication.testCodes

import android.util.Log

object TimeTest {
    var start:Long = 0
    var end:Long = 0

    fun start(){
        start = System.currentTimeMillis()
    }

    fun end(){
        end = System.currentTimeMillis()
    }

    fun calculate(){
        Log.e("time ---:::: " ,"${start - end}")
    }
}