package com.example.myapplication.controler.productsAndMeals

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.myapplication.R
import com.example.myapplication.controler.interfaces.HistoryListView
import com.example.myapplication.module.Meals
import kotlinx.android.synthetic.main.component_meal_tile.view.*

class MealHistoryAdaptor(private val c: Context, private val list:ArrayList<Meals>, private val mealInterface: HistoryListView?= null):BaseAdapter() {
    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(c).inflate(R.layout.component_meal_tile,null)
        val meal = list[position]

        if(!meal.mealState.isBlank()){
            view.tv_mealState.text = meal.mealState
        }else{
            view.tv_mealState.text = ""
        }

        view.tv_price.text = meal.mealPrice.toString()

        if (!meal.mealState.isBlank()){
            view.tv_date.text = meal.mealDate

        }else{
            view.tv_date.text = ""
        }

        if (mealInterface != null){
            view.btn_removeMealHistory.setOnClickListener {
                mealInterface.removeMeal(meal.mealId)
            }
        }


        return  view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}