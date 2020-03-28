package com.example.myapplication.controler

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object Utils{

    fun day():String{
        val d = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd")
        return sdf.format(d)
    }

    fun hour():String{
        val d = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh")
        return sdf.format(d)
    }

    fun date():String{
        val d = Calendar.getInstance().time
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy h:mm a")
        return sdf.format(d)
    }

    fun mealKey(id:String):String{
        return id+date()
    }

}