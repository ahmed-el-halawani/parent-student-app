package com.example.myapplication.ui.parentUi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.R
import com.example.myapplication.controler.*
import com.example.myapplication.controler.interfaces.ListViewInterFaces
import com.example.myapplication.controler.parent.StudentListView
import com.example.myapplication.module.Parents
import com.example.myapplication.module.Student
import com.example.myapplication.ui.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.parent_activity_main.*
import kotlinx.android.synthetic.main.dialog_parent_add_son.view.*
import kotlinx.android.synthetic.main.component_parent_child_tile.view.*

@SuppressLint("InflateParams","SetTextI18n")
class ParentMainActivity : AppCompatActivity(),
    ListViewInterFaces {

    private var endApp = 0
    private var showHideState = 0
    private lateinit var auth:FirebaseAuth
    private lateinit var studentRef:DatabaseReference
    private lateinit var parentRef:DatabaseReference
    private lateinit var preView:View
    private lateinit var cu:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_activity_main)
        auth = FirebaseAuth.getInstance()
        studentRef = OfflineUser.database.child("student")
        parentRef = OfflineUser.database.child("parent")
        cu = auth.currentUser!!.uid


    }

    override fun onStart() {
        super.onStart()
        getStudent()

        btn_addSon.setOnClickListener {
            addStudent()
        }

        parentRef.child(cu).addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val parent = p0.getValue(Parents::class.java)!!
                tv_cash.text = parent.cash.toString() + " EGP"

            }

        })

        btn_profile.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun addStudent(){

        val re = dialogBuilder()

        re.view.btn_add.setOnClickListener {
            val email = re.view.et_email.text.toString()
            val password = re.view.et_password.text.toString()

            studentRef.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for(i in p0.children){
                        val student = i.getValue(Student::class.java)!!
                        if(student.email == email && student.password == password){
                            when(student.parent){
                                ""->{
                                    studentRef.child(i.key!!)
                                        .child("parent")
                                        .setValue(cu)

                                    parentRef.child(cu)
                                        .child("children")
                                        .child(student.id)
                                        .setValue(student.id)
                                    Toast.makeText(baseContext, "done", Toast.LENGTH_LONG).show()
                                    re.alertDialog.dismiss()
                                }
                                cu ->Toast.makeText(baseContext,"this student is already here",Toast.LENGTH_LONG).show()
                                else  ->Toast.makeText(baseContext,"this student added to another parent app",Toast.LENGTH_LONG).show()

                            }
                        }
                    }
                }

            })

        }

        re.view.btn_cancel.setOnClickListener {
            re.alertDialog.dismiss()
        }
    }

    class DRes(var view:View, var alertDialog:AlertDialog)//dialog recurse
    private fun dialogBuilder():DRes{
        val dialog = AlertDialog.Builder(this)

        val view = layoutInflater.inflate(R.layout.dialog_parent_add_son,null)
        dialog.setView(view)
        val c = dialog.create()
        c.show()

        return DRes(view,c)
    }

    private fun getStudent(){
        val cu = cu


        OfflineUser.database.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val studentPath =  p0.child(Keys.parent).child(cu).child(Keys.children)
                val parent = p0.child(Keys.parent).child(cu).getValue(Parents::class.java)!!
                val studentDataSnapshot = p0.child(Keys.student)

                val studentsList = ArrayList<Student>()
                if(studentPath.exists()){
                    for (i in parent.children){
                        if(studentDataSnapshot.child(i.value).exists()){
                            val student = studentDataSnapshot.child(i.value).getValue(Student::class.java)!!
                            studentsList.add(student)
                        }
                    }
                }

                lv_students.adapter = StudentListView(
                    this@ParentMainActivity,
                    studentsList,
                    this@ParentMainActivity
                )

            }

        })
    }

    override fun removeStudent(position: String) {
        studentRef.child(position).child("parent").setValue("")
        parentRef.child(cu).child("children").child(position).removeValue()
    }

    override fun showHideButton(view: View) {
        when {
            showHideState == 0 -> {
                view.btn_remove.visibility = View.VISIBLE
                preView = view
                showHideState++
            }
            view == preView -> {
                view.btn_remove.visibility = View.GONE
                showHideState--
            }
            else -> {
                view.btn_remove.visibility = View.VISIBLE
                preView.btn_remove.visibility = View.GONE
                preView = view
            }
        }
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

}





















//            studentRef.child(i.key!!).child("parent").setValue("parent")






//            for(i in studentDataBase.studentsList()){
//                if(i.email == email && i.password == password){
//                    if(i.parent == ""){
//                        i.parent = auth.userName()
//                        auth.addStudent(i)
//                        t = 1
//                        refresh()
//                        break
//                    }else if(i.parent == auth.userName()){
//                        t=2
//                        break
//                    }else{
//                        t=3
//                        break
//                    }
//
//                }
//            }
//            if(t == 0) {
//                Toast.Lib(this, "email or password are wrong", Toast.LENGTH_LONG).show()
//            }else if(t == 1){
//                Toast.Lib(this,"added",Toast.LENGTH_LONG).show()
//                c.dismiss()
//            }else if(t ==2){
//                Toast.Lib(this,"this student is already here",Toast.LENGTH_LONG).show()
//            }else{
//                Toast.Lib(this,"this student added to another parent app",Toast.LENGTH_LONG).show()
//            }




//    fun onstart(){
//        tv_cash.text = auth.cash().toString() + "EGP"
//
//        btn_profile.setOnClickListener {
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
//        }
//
//        btn_addSon.setOnClickListener {
//            addStudent()
//        }
//
//        refresh()
//    }
