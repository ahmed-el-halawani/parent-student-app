package com.example.myapplication.ui.studentUi


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.productsAndMeals.MealHistoryAdaptor
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.interfaces.HistoryListView
import com.example.myapplication.module.Meals
import com.example.myapplication.module.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.parent_student_fragment_meal_history.*

class MealHistoryStudentFragment : Fragment(){

    lateinit var auth: FirebaseAuth
    lateinit var studentId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.parent_student_fragment_meal_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        studentId = OfflineUser.studentId

        refresh()
    }

    private fun refresh(){
        val mealList:ArrayList<Meals> = ArrayList()
        OfflineUser.database.child(Keys.student).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                mealList.clear()
                val studentMealPath = p0.child(studentId).child(Keys.meals)
                if (studentMealPath.exists()){
                    for(i in studentMealPath.children){
                        val state = i.child(Keys.mealState).value as String
                        if(state == "Done" || state == "Failed"){
                            val meal = i.getValue(Meals::class.java)
                            mealList.add(meal!!)
                        }
                    }
                }

                lv_mealsHistory.adapter =
                    MealHistoryAdaptor(
                        activity!!.baseContext,
                        mealList
                    )


            }

        })

    }




}
