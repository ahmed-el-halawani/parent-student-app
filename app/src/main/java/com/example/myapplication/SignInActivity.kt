package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.module.Student
import com.example.myapplication.ui.parentUi.ParentMainActivity
import com.example.myapplication.ui.studentUi.StudentMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_signin.*
import java.lang.Exception

class SignInActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var storage: StorageReference
    private var user = "parent"
    private lateinit var emailTextView:TextView
    private lateinit var passwordTextView:TextView
    private lateinit var userDatabase:DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
       

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance().reference
        emailTextView = et_email
        passwordTextView = et_password
        userDatabase = database.reference
        OfflineUser.database = userDatabase

    }

    override fun onStart() {
        super.onStart()
        val p = signInParent.textColors
        val s = signInStudent.textColors
        toggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.signInParent->{
                    signInStudent.setTextColor(s)
                    signInParent.setTextColor(p)
                    user = "parent"
                }
                R.id.signInStudent->{
                    signInStudent.setTextColor(p)
                    signInParent.setTextColor(s)
                    user = "student"
                }
            }
        }

        btn_signIn.setOnClickListener {
            signInModel()
        }//end of signIn btn

        btn_testUser.setOnClickListener {
            signInModel(true)
        }

        btn_signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }


    private fun signInModel(testMode:Boolean = false){
        lo_progressBar.visibility = View.VISIBLE

        val email: String
        val password: String
        OfflineUser.rule = user

        if(testMode && user == Keys.parent) {
            email = "agomaa@gmail.com"
            password = "12345678"
        }else if(testMode && user == Keys.student){
            email = "user1@g.com"
            password = "12345678"
        }else{
            email = emailTextView.text.toString()
            password = passwordTextView.text.toString()
        }



        //empty chick
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    userDatabase.child(user).child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(child: DataSnapshot) {
                            if (child.exists()) {
                                if (user == "parent") {
                                    val intent = Intent(
                                        this@SignInActivity,
                                        ParentMainActivity::class.java
                                    )
                                    OfflineUser.parentId = auth.currentUser!!.uid
                                    startActivity(intent)
                                    finishAffinity()
                                }else{//end of specifying is parent or student

                                    val student = child.getValue(Student::class.java)!!
                                    OfflineUser.name = student.name
                                    OfflineUser.cash = 0.0
                                    val intent = Intent(
                                        this@SignInActivity,
                                        StudentMainActivity::class.java
                                    )
                                    OfflineUser.studentId = auth.currentUser!!.uid
                                    OfflineUser.parentId = student.parent
                                    startActivity(intent)
                                    finishAffinity()

                                }//end of else specifying is parent or student
                            }else{//end of found parent or student
                                lo_progressBar.visibility = View.GONE
                                tv_ruleError.visibility = View.VISIBLE
                                et_email.error = "wrong email"
                                et_password.error = "wrong password"

                            }//end of else found parent or student
                        }// end of dataSnapShot
                    })//end of addListenerForSingleValue
                }else{//end of isSuccess
                    lo_progressBar.visibility = View.GONE
                    try {
                        val error = it.exception as FirebaseAuthException
                        tv_ruleError.visibility = View.VISIBLE
                        when (error.errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                et_email.error = "wrong email"
                            }
                            "EMAIL_MISMATCH_ERROR " -> {
                                et_email.error = "email should be like this example@example.com"
                                et_password.error = "wrong password"
                                tv_ruleError.visibility = View.VISIBLE
                            }
                            "ERROR_USER_NOT_FOUND"->{
                                et_email.error = "wrong email"
                                et_password.error = "wrong password"
                            }
                            "ERROR_WRONG_PASSWORD"->{
                                et_password.error = "Wrong password"
                            }
                            else->{
                                Toast.makeText(baseContext,error.errorCode,Toast.LENGTH_LONG).show()
                            }
                        }
                    }catch (e:Exception){
                        Toast.makeText(baseContext,"you are not connect chick your wifi or data",Toast.LENGTH_LONG).show()
                    }

                }//end of else isSuccess
            }//end of addOnCompleteListener
        }else{//end of empty chick
            lo_progressBar.visibility = View.GONE
            et_email.error = "wrong email"
            et_password.error = "wrong password"
        }//end of else empty chick
    }

    override fun onBackPressed() {
        finishAffinity()
    }

}
