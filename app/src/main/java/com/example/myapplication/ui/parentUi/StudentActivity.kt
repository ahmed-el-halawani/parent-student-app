package com.example.myapplication.ui.parentUi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.parent.ParentStudentViewPager
import com.example.myapplication.ui.parentUi.studentFragment.MealHistoryFragment
import com.example.myapplication.ui.parentUi.studentFragment.StudentMealControlFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.parent_activity_student.*
import kotlinx.android.synthetic.main.component_custom_action_bar.*

class StudentActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth
    lateinit var studentId:String
    private lateinit var pagesList:ArrayList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_activity_student)

        val extra = intent.extras!!
        studentId = extra.getString(Keys.studentId)!!


        OfflineUser.studentId = studentId

        btn_back.setOnClickListener {
            onBackPressed()
        }

        btn_parentStudentProfile.setOnClickListener {
            val intent = Intent(this,StudentProfileActivity::class.java)
            startActivity(intent)
        }




        auth = FirebaseAuth.getInstance()
        pagesList = arrayListOf(StudentMealControlFragment(),MealHistoryFragment())

    }

    override fun onStart() {
        super.onStart()
        vp_parentStudentViewPagerLayout.adapter = ParentStudentViewPager(this,pagesList,supportFragmentManager)
        tl_parentStudentTapLayout.setupWithViewPager(vp_parentStudentViewPagerLayout)
    }
}
