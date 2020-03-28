package com.example.myapplication.ui.studentUi


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.productsAndMeals.MealAdaptor
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.interfaces.MealAdaptorInterface
import com.example.myapplication.module.Product
import com.example.myapplication.module.Student
import com.example.myapplication.ui.marketUi.MarketActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.student_activity_meal.*

@SuppressLint("SetTextI18n")
class MealActivity : Fragment() ,MealAdaptorInterface{
    private lateinit var auth:FirebaseAuth
    private lateinit var studentId:String
    private lateinit var qrCodeImage:Bitmap
    private lateinit var qrgEncoder:QRGEncoder


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studentId = OfflineUser.studentId
        return inflater.inflate(R.layout.student_activity_meal,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        mealList()
        qrCodeCreator()
        val p = mealListView.textColors
        val s = qrCode.textColors
        
        toggle.setOnCheckedChangeListener { _, id ->
            when(id){
                R.id.mealListView ->{
                    mealContainer.visibility = View.VISIBLE
                    qrcodeContainer.visibility = View.GONE
                    mealListView.setTextColor(p)
                    qrCode.setTextColor(s)

                }
                R.id.qrCode ->{
                    qrcodeContainer.visibility = View.VISIBLE
                    mealContainer.visibility = View.GONE
                    mealListView.setTextColor(s)
                    qrCode.setTextColor(p)
                }
            }

        }
    }

    private fun mealList(){
        val context = activity!!.baseContext

        fb_addMeal.setOnClickListener{
            val intent = Intent(context, MarketActivity::class.java)
            startActivity(intent)
        }

        OfflineUser.database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentMeal = p0.child(Keys.student).child(studentId).child(Keys.currentMeal)
                if ((currentMeal.value as String).isNotEmpty()) {
                    val mealName = currentMeal.value as String
                    val mealPath = p0.child(Keys.student).child(auth.uid!!).child(Keys.meals).child(mealName)
                    val mealList: ArrayList<Pair<String, Product>> = ArrayList()

                    if (mealPath.exists()) {
                        val state = mealPath.child(Keys.mealState).value as String
                        if (state == Keys.inProgress) {
                            val mealProductPath = mealPath.child(Keys.mealProducts)
                            val student =
                                p0.child(Keys.student).child(auth.uid!!).getValue(Student::class.java)!!
                            val meal = student.meals[mealName]
                            for (i in mealProductPath.children) {
                                val product = i.getValue(Product::class.java)!!
                                val k = i.key!!
                                mealList.add(Pair(k, product))
                            }
                            tv_mealPrice.text = meal!!.mealPrice.toString() + " EGP"
                        }
                    }
                    lv_meals.adapter = MealAdaptor(
                        context,
                        mealList,
                        this@MealActivity
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
                if (currentMeal.exists()){
                    val mealKey = currentMeal.value as String
                    val mealPath = OfflineUser.database.child(Keys.student)
                        .child(studentId).child(Keys.meals).child(mealKey)

                    val meal = p0.child(Keys.meals).child(mealKey)
                    val studentCash = p0.child(Keys.cash).value as Long
                    val price = meal.child(Keys.mealPrice).value as Long
                    val removedProduct = meal.child(Keys.mealProducts).child(id).child(Keys.productPrice).value as Long
                    val newPrice = price - removedProduct
                    mealPath.child(Keys.mealPrice).setValue(newPrice)
                    mealPath.child(Keys.mealProducts).child(id).removeValue()
                    studentPath.child(Keys.cash).setValue(studentCash+removedProduct)
                    mealList()
                }

            }
        })
    }

    private fun qrCodeCreator(){

        val student = OfflineUser.database.child(Keys.student).child(studentId)
        val x = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(x)

        student.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentMeal = p0.child(Keys.currentMeal)
                if (currentMeal.exists()){
                    val mealName = currentMeal.value as String
                    if (p0.child(Keys.meals).child(mealName).exists()){
                        qrgEncoder = QRGEncoder(
                            mealName,null,QRGContents.Type.TEXT,x.widthPixels
                        )
                        try {
                            qrCodeImage = qrgEncoder.encodeAsBitmap()
                            iv_qrcode.setImageBitmap(qrCodeImage)
                        }catch(e:Exception) {
                            Log.e("Error in qrCode" , e.message.toString())
                        }
                    }
                }

            }

        })
    }


}
