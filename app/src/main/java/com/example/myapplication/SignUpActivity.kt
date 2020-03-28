package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.module.Parents
import com.example.myapplication.module.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private var uri:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()

        parentTap()
        val p = rb_parent.textColors
        val s = rb_student.textColors
        toggle.setOnCheckedChangeListener { _, checkedId ->

                when (checkedId) {
                R.id.rb_parent -> {
                    lo_parent.visibility = View.VISIBLE
                    lo_child.visibility = View.GONE
                    rb_parent.setTextColor(p)
                    rb_student.setTextColor(s)

                    parentTap()
                }
                R.id.rb_student ->{
                    lo_parent.visibility = View.GONE
                    lo_child.visibility = View.VISIBLE
                    rb_parent.setTextColor(s)
                    rb_student.setTextColor(p)
                    studentTap()
                }
            }
        }
    }

    private fun parentTap(){

        iv_profileRegister.setOnClickListener {
            val getPhoto = Intent()
            getPhoto.type = "image/*"
            getPhoto.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(getPhoto,1)
        }

        btn_signUpRegister.setOnClickListener {
            val userName = et_parentNameRegister.text.toString()
            val email = et_parentEmailRegister.text.toString()
            val password = et_parentpasswordRegister.text.toString()
            val confirmPassword = et_parentpasswordRegister2.text.toString()
            var image:String? = null
            lo_progressBar.visibility = View.VISIBLE

            if (email.isEmpty() && password.isEmpty()) {
                lo_progressBar.visibility = View.GONE
                et_parentpasswordRegister.error = "password must be not empty"
                et_parentEmailRegister.error = "email must be not empty"



            }else if(password.length < 8 || password.length > 14) {
                lo_progressBar.visibility = View.GONE
                et_parentpasswordRegister.error = "password should be between 8-14"

            }else  if(password != confirmPassword){
                lo_progressBar.visibility = View.GONE
                et_parentpasswordRegister2.error = "not match the password"
            }else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { user ->
                    if (user.isSuccessful) {
                        val storage = FirebaseStorage.getInstance().reference
                        val x = OfflineUser.database.child("parent")


                        fun createParent() {
                            x.child(auth.uid!!).setValue(
                                Parents(
                                    auth.uid!!,
                                    email,
                                    password,
                                    userName,
                                    0.0,
                                    image!!,
                                    "parent"
                                )
                            )
                            lo_progressBar.visibility = View.GONE
                              auth.signOut()
                              onBackPressed()
                        }



                        if (uri != null) {
                            val imageName = "parentImages/" + UUID.randomUUID().toString()
                            val upLoadImage = storage.child(imageName)

                            upLoadImage.putFile(uri!!)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        image = imageName
                                        createParent()
                                    }
                                }
                        } else {
                            image = "parentImages/profile_image.jpg"
                            createParent()
                        }

                    } else {
                        lo_progressBar.visibility = View.GONE


                        val error = user.exception as FirebaseAuthException
                        when (error.errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                et_parentEmailRegister.error = "wrong email"
                            }
                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                et_parentEmailRegister.error = "email is already in use"
                            }
                        }
                    }
                }
            }

        }
    }

    private fun studentTap(){

        iv_studentProfileRegister.setOnClickListener {
            val getPhoto = Intent()
            getPhoto.type = "image/*"
            getPhoto.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(getPhoto,1)
        }

        btn_signUpRegister.setOnClickListener {
            val userName = et_studentNameRegister.text.toString()
            val email = et_studentEmailRegister.text.toString()
            val password = et_studentPasswordRegister.text.toString()
            val confirmPassword = et_studentPasswordRegister2.text.toString()
            var image:String? = null


            if (email.isEmpty() && password.isEmpty()) {
                lo_progressBar.visibility = View.GONE
                et_studentPasswordRegister.error = "password must be not empty"
                et_studentEmailRegister.error = "email must be not empty"

            }else if(password != confirmPassword) {
                et_studentPasswordRegister2.error = "not match the password"
            }else if(password.length < 8 || password.length > 14) {
                et_studentPasswordRegister.error = "password should be between 8-14"
            }else {
                lo_progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val storage = FirebaseStorage.getInstance().reference

                        fun createStudent() {
                            val x = OfflineUser.database.child("student")
                            x.child(auth.uid!!)
                                .setValue(
                                    Student(
                                        auth.uid!!,
                                        userName,
                                        image!!,
                                        email,
                                        password,
                                        "",
                                        "free"
                                    )
                                )
                            lo_progressBar.visibility = View.GONE
                            auth.signOut()
                            onBackPressed()

                        }

                        if (uri != null) {
                            val imageName = "studentImage/" + UUID.randomUUID().toString()
                            val upLoadImage = storage.child(imageName)
                            upLoadImage.putFile(uri!!)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        image = imageName
                                        createStudent()
                                    }
                                }
                        } else {
                            image = "studentImage/profile_image.jpg"
                            createStudent()
                        }

                    } else {
                        lo_progressBar.visibility = View.GONE

                        val error = task.exception as FirebaseAuthException
                        when (error.errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                et_studentEmailRegister.error = "wrong email"
                                et_studentPasswordRegister.error = "wrong password"
                            }
                            "ERROR_WRONG_PASSWORD" -> {
                                et_studentPasswordRegister.error = "wrong password"
                            }
                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                et_studentEmailRegister.error = "email is already in use"
                            }
                        }
                    }
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK && data !=null){
            uri = data.data
            Picasso.get().load(uri).into(iv_profileRegister)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(baseContext,SignInActivity::class.java))
        finishAffinity()
    }


}
