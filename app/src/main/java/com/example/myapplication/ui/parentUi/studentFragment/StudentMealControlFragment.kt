package com.example.myapplication.ui.parentUi.studentFragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.productsAndMeals.MealAdaptor
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.Utils
import com.example.myapplication.controler.interfaces.MealAdaptorInterface
import com.example.myapplication.module.Product
import com.example.myapplication.module.Student
import com.example.myapplication.ui.marketUi.MarketActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.parent_fragment_student_meal_item.*

@SuppressLint("SetTextI18n")
class StudentMealControlFragment : Fragment(),MealAdaptorInterface {


    lateinit var utils:Utils
    lateinit var studentId:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.parent_fragment_student_meal_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils
        studentId = OfflineUser.studentId
    }

    override fun onStart() {
        super.onStart()
        showList()
        goToMarket()
    }

    private fun goToMarket(){
        val k = Keys
        val rulePath = OfflineUser.database.child(k.student).child(studentId)
        rulePath.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            @SuppressLint("RestrictedApi")
            override fun onDataChange(p0: DataSnapshot) {
                val rule = p0.child(k.rule).value as String
                if (rule == k.controlRule){
                    fb_addMealForStudent.visibility = View.VISIBLE
                    fb_addMealForStudent.setOnClickListener {
                        val intent = Intent(activity!!.baseContext,MarketActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    fb_addMealForStudent.visibility = View.GONE
                }
            }

        })

    }

    fun showList(){

            val context = activity!!.baseContext

            OfflineUser.database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val currentMeal = p0.child("student").child(studentId).child(Keys.currentMeal)
                    if (currentMeal.exists()) {
                        val mealName = currentMeal.value as String
                        val mealPath =
                            p0.child("student").child(studentId).child("meals").child(mealName)
                        val mealList: ArrayList<Pair<String, Product>> = ArrayList()
                        if (mealPath.exists()) {
                            val mealProductPath = mealPath.child("mealProducts")
                            val student =
                                p0.child("student").child(studentId).getValue(Student::class.java)!!
                            val meal = student.meals[mealName]
                            for (i in mealProductPath.children) {
                                val product = i.getValue(Product::class.java)!!
                                val k = i.key!!
                                mealList.add(Pair(k, product))
                            }
                            tv_mealPriceFromParent.text = meal!!.mealPrice.toString() + " EGP"
                        }
                        lv_productsList.adapter = MealAdaptor(
                            context,
                            mealList,
                            this@StudentMealControlFragment
                        )
                    }
                }
            })
    }

    override fun removeProduct(id: String) {

        val studentPath = OfflineUser.database.child("student").child(studentId)
        studentPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentMeal = p0.child(Keys.currentMeal)
                if (currentMeal.exists()) {
                    val mealId = currentMeal.value as String
                    val mealPath = OfflineUser.database.child("student")
                        .child(studentId).child("meals").child(mealId)

                    val meal = p0.child(Keys.meals).child(mealId)
                    val studentCash = p0.child(Keys.cash).value as Long
                    val price = meal.child(Keys.mealPrice).value as Long
                    val removedProduct =
                        meal.child(Keys.mealProducts).child(id).child(Keys.productPrice).value as Long

                    val newPrice = price - removedProduct
                    mealPath.child(Keys.mealPrice).setValue(newPrice)
                    mealPath.child(Keys.mealProducts).child(id).removeValue()
                    studentPath.child(Keys.cash).setValue(studentCash + removedProduct)
                    showList()
                }
            }
        })

    }

}
