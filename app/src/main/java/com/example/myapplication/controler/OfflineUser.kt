package com.example.myapplication.controler

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference


object OfflineUser{
    lateinit var rule:String
    lateinit var name:String
    var cash = 0.0
    lateinit var studentId: String
    lateinit var parentId:String
    lateinit var database:DatabaseReference
}
