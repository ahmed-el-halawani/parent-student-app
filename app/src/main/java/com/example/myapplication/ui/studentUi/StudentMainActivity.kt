package com.example.myapplication.ui.studentUi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.controler.student.StudentPagerView
import com.example.myapplication.ui.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.student_activity_main.*

class StudentMainActivity : AppCompatActivity() {
    private var endApp = 0
    private lateinit var auth:FirebaseAuth
    private lateinit var pageList:ArrayList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_activity_main)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        btn_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        pageList = arrayListOf(MealActivity(),MealHistoryStudentFragment())
        VB_studentPages.adapter = StudentPagerView(
            this,
            pageList,
            supportFragmentManager
        )
        tl_studentPagerView.setupWithViewPager(VB_studentPages)
    }

    override fun onBackPressed() {
        endApp += 1
        if(endApp == 1){
            Toast.makeText(this,"please click two time to end", Toast.LENGTH_SHORT).show()
            val mHandler = Handler()
            val mRunnable = Runnable{
                endApp = 0
            }
            mHandler.postDelayed(mRunnable,5000)
        }else{
            auth.signOut()
            finishAffinity()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }

}
