package com.example.myapplication.testCodes

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class Lib(val c:Context){
    fun mprint(text:String){
        Toast.makeText(c,text,Toast.LENGTH_LONG).show()
    }


}