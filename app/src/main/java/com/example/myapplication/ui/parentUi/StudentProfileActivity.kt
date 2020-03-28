package com.example.myapplication.ui.parentUi

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.module.Student
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.Utils
import com.example.myapplication.testCodes.Lib
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.parent_activity_student_profile.*
import kotlinx.android.synthetic.main.component_custom_action_bar.*
import kotlinx.android.synthetic.main.dialog_parent_send_money.view.*
import kotlinx.android.synthetic.main.dialog_parent_send_money.view.et_moneyNumber
import kotlinx.android.synthetic.main.dialog_parent_student_rule.view.*
import java.lang.Exception

@SuppressLint("SetTextI18n","InflateParams")
class StudentProfileActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var storage:StorageReference
    private lateinit var rule:String
    private lateinit var mPrint:Lib
    private lateinit var studentPath:DatabaseReference
    private lateinit var parentPath:DatabaseReference
    private lateinit var u:Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_activity_student_profile)
        u = Utils
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance().reference
        rule = OfflineUser.rule
        mPrint = Lib(this)
        studentPath = OfflineUser.database.child(Keys.student).child(OfflineUser.studentId)
        parentPath = OfflineUser.database.child(Keys.parent).child(OfflineUser.parentId)

    }

    override fun onStart() {
        super.onStart()
        backButton()


        studentPath.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val student = p0.getValue(Student::class.java)!!
                    studentInfo(student)
                    editRule(student)
                    sendMoney(student)

                }catch (e:Exception){
                    Log.e("Error message" , e.message.toString())
                }

            }
        })
    }

    private fun studentInfo(student: Student){
        tv_nameParentStudent.text = student.name
        tv_studentRule.text = student.rule
        tv_emailParentStudent.text = student.email
        tv_cashParentStudent.text = "${student.cash} EGP"


        storage.child(student.img).downloadUrl.addOnSuccessListener { uri->
            val x = Picasso.get()
                .load(uri)

            x.into(iv_profileBg)
            x.transform(BlurTransformation(this@StudentProfileActivity,15,1))
                .into(iv_profileParentStudent)

        }
    }

    private fun editRule(student: Student){
        btn_editRule.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_parent_student_rule,null)
            dialog.setView(dialogView)
            val container = dialog.create()
            container.show()

            val radioGroup = dialogView.rg_editRule

            when(student.rule){
                Keys.freeRule-> radioGroup.check(R.id.freeToBuy)
                Keys.controlRule-> radioGroup.check(R.id.controlled)
            }




            dialogView.btn_doneEditRule.setOnClickListener {
                when(radioGroup.checkedRadioButtonId){
                    R.id.freeToBuy->{
                        studentPath.child(Keys.rule).setValue(Keys.freeRule)
                        Toast.makeText(this,Keys.freeRule, Toast.LENGTH_SHORT).show()

                    }
                    R.id.controlled->{
                        studentPath.child(Keys.rule).setValue(Keys.controlRule)
                        Toast.makeText(this,Keys.controlRule, Toast.LENGTH_SHORT).show()

                    }
                }
                container.dismiss()
            }

            dialogView.btn_cancelDialog.setOnClickListener {
                container.dismiss()
            }
        }
    }

    private fun sendMoney(student: Student){
        btn_sendMonyToStudent.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_parent_send_money,null)
            dialog.setView(view)
            val showDialog = dialog.create()
            showDialog.show()

            view.btn_sendMoney.setOnClickListener {


                parentPath.child(Keys.cash).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val moneyEditText = view.et_moneyNumber
                        val money = moneyEditText.text.toString().trim().toDouble()
                        val parentMoney = p0.value as Long
                        when {
                            money<=0 -> moneyEditText.error = "money most be more then 0"
                            parentMoney < money -> moneyEditText.error = "u don't have enough money"
                            else -> {
                                val studentMoney = student.cash
                                val newMoney = money+studentMoney
                                studentPath.child(Keys.cash).setValue(newMoney)
                                parentPath.child(Keys.cash).setValue(parentMoney-money)
                                showDialog.dismiss()
                            }
                        }
                    }

                })

            }

            view.btn_cancel.setOnClickListener {
                showDialog.dismiss()
            }

        }
    }

    private fun backButton(){
        btn_back.setOnClickListener {
            finish()
        }
    }

}
