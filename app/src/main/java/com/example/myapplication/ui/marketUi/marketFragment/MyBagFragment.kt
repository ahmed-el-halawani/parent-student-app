package com.example.myapplication.ui.marketUi.marketFragment


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.Utils
import com.example.myapplication.controler.interfaces.MealAdaptorInterface
import com.example.myapplication.controler.productsAndMeals.MealAdaptor
import com.example.myapplication.module.Product
import com.example.myapplication.module.Student
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.market_fragment_mybag.*

@SuppressLint("SetTextI18n")
class MyBagFragment : Fragment(), MealAdaptorInterface {


    private lateinit var studentId:String
    private lateinit var parentId:String
    private lateinit var utils:Utils



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.market_fragment_mybag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentId = OfflineUser.studentId
        parentId = OfflineUser.parentId
        utils = Utils

    }

    override fun onStart() {
        super.onStart()
        showMealProducts()
    }

    private fun showMealProducts(){
        OfflineUser.database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentMeal = p0.child(Keys.currentMeal)
                if (currentMeal.exists()) {
                    val mealId = currentMeal.value as String
                    val mealPath = p0.child("student").child(studentId).child("meals").child(mealId)
                    val mealList: ArrayList<Pair<String, Product>> = ArrayList()

                    if (mealPath.exists()) {
                        val mealProductPath = mealPath.child("mealProducts")
                        val student =
                            p0.child("student").child(studentId).getValue(Student::class.java)!!
                        val meal = student.meals[mealId]
                        for (i in mealProductPath.children) {
                            val product = i.getValue(Product::class.java)!!
                            val k = i.key!!
                            mealList.add(Pair(k, product))
                        }
                        tv_myBagPrice.text = meal!!.mealPrice.toString() + " EGP"
                    }
                    lv_myBagList.adapter = MealAdaptor(
                        context!!,
                        mealList,
                        this@MyBagFragment
                    )
                }
            }
        })
    }

    override fun removeProduct(id: String) {
        val studentPath = OfflineUser.database.child("student").child(studentId)

        studentPath.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentMeal = p0.child(Keys.currentMeal)
                if (currentMeal.exists()) {
                    val mealId = currentMeal.value as String
                    val mealPath = OfflineUser.database.child(Keys.student).child(studentId).child(Keys.meals).child(mealId)
                    val meal = p0.child(Keys.meals).child(mealId)
                    val studentCash = p0.child(Keys.cash).value as Long
                    val price = meal.child(Keys.mealPrice).value as Long
                    val removedProduct =
                        meal.child(Keys.mealProducts).child(id).child(Keys.productPrice).value as Long
                    val newPrice = price - removedProduct
                    mealPath.child(Keys.mealPrice).setValue(newPrice)
                    mealPath.child(Keys.mealProducts).child(id).removeValue()
                    studentPath.child(Keys.cash).setValue(studentCash + removedProduct)
                    showMealProducts()
                }
            }
        })
    }
}
