package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.SignInActivity
import com.example.myapplication.module.Parents
import com.example.myapplication.module.Student
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.testCodes.Lib
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.parent_student_activity_profile.*
import kotlinx.android.synthetic.main.component_custom_action_bar.*

@SuppressLint("SetTextI18n")
class ProfileActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var storage:StorageReference
    private lateinit var rule:String
    private lateinit var mPrint:Lib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_student_activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance().reference

        rule = OfflineUser.rule
        mPrint = Lib(this)


    }


    override fun onStart() {
        super.onStart()

        btn_logOt.setOnClickListener {
            auth.signOut()
            finishAffinity()
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }


        if(rule == "parent"){
            tv_cash.text = "you haven't money"
            database.getReference("parent").child(auth.currentUser?.uid!!).addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val parent = p0.getValue(Parents::class.java)
                    ruleParent(parent!!)
                }
            })
        }else{
            tv_cash.text = "you haven't money"
            database.getReference("student").child(auth.currentUser?.uid!!).addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val student = p0.getValue(Student::class.java)!!
                    ruleStudent(student)
                }
            })
        }
    }

    private fun ruleParent(parent: Parents){
        tv_name.text = parent.name
        storage.child(parent.image).downloadUrl.addOnSuccessListener {uri->
            val x = Picasso.get()
                .load(uri)

            x.into(iv_profileBg)
            x.transform(BlurTransformation(this@ProfileActivity,15,1))
                .into(iv_profile)

        }




        tv_email.text = parent.email
        tv_cash.text = "${parent.cash} EGP"

        OfflineUser.cash = parent.cash
        OfflineUser.name = parent.name
    }

    private fun ruleStudent(student: Student){
        storage.child(student.img).downloadUrl.addOnSuccessListener { uri ->
            val x = Picasso.get()
                .load(uri)

            x.into(iv_profileBg)
            x.into(iv_profile)
        }



        tv_name.text = student.name
        tv_email.text = student.email
        tv_cash.text = student.cash.toString() + " EGP"

        OfflineUser.name = student.name
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }



}
