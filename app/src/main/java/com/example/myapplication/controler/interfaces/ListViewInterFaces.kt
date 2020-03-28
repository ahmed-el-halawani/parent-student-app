package com.example.myapplication.controler.interfaces

import android.view.View

interface ListViewInterFaces {
    fun removeStudent(position:String)
    fun showHideButton(view:View)
}

interface HistoryListView{
    fun removeMeal(id:String)
}

interface MealAdaptorInterface{
    fun removeProduct(id:String)
}