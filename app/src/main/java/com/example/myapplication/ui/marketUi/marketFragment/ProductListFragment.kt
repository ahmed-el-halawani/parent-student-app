package com.example.myapplication.ui.marketUi.marketFragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.interfaces.ProductListInterface
import com.example.myapplication.controler.market.MarketListView
import com.example.myapplication.controler.Utils
import com.example.myapplication.module.Meals
import com.example.myapplication.module.Product
import com.example.myapplication.module.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.market_fragment_product.*

class ProductListFragment : Fragment() , ProductListInterface {

    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var studentId:String
    private lateinit var productAdapter:ListAdapter
    private lateinit var productsList:ArrayList<Product>
    private lateinit var utils:Utils
    private lateinit var con:Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        studentId = OfflineUser.studentId
        productsList = ArrayList()
        utils = Utils
        return inflater.inflate(R.layout.market_fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onStart() {
        super.onStart()
        con = this.context!!

        database.getReference("product").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val products = p0.children
                for (i in products){
                    val product = i.getValue(Product::class.java)!!
                    productsList.add(product)
                }
                productAdapter = MarketListView(
                    activity!!.baseContext,
                    productsList,
                    "L",
                    this@ProductListFragment as ProductListInterface
                )
                listView()
            }

        })

        btn_listView.setOnClickListener {
            productAdapter = MarketListView(
                activity!!.baseContext,
                productsList,
                "L",
                this@ProductListFragment as ProductListInterface
            )
            listView()
        }

        btn_gridView.setOnClickListener {
            productAdapter = MarketListView(
                activity!!.baseContext,
                productsList,
                "G",
                this@ProductListFragment as ProductListInterface
            )
            gridView()
        }



    }

    private fun gridView(){
        lv_marketProductList.visibility = View.GONE
        lv_marketProductGrid.visibility = View.VISIBLE
        btn_gridView.background.setTint(ContextCompat.getColor(this.context!!,R.color.buttonColor))
        btn_listView.background.setTint(ContextCompat.getColor(this.context!!,R.color.buttonUnChickColor))
        lv_marketProductGrid.adapter = productAdapter
    }

    private fun listView(){
        lv_marketProductList.visibility = View.VISIBLE
        lv_marketProductGrid.visibility = View.GONE
        btn_gridView.background.setTint(ContextCompat.getColor(this.context!!,R.color.buttonUnChickColor))
        btn_listView.background.setTint(ContextCompat.getColor(this.context!!,R.color.buttonColor))
        lv_marketProductList.adapter = productAdapter

    }

    override fun addProduct(barcode: String) {
        OfflineUser.database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val products = p0.child("product").child(barcode)
                if(products.exists()){
                    val product = products.getValue(Product::class.java)!!
                    val cashPath = database.reference.child("student").child(studentId).child(Keys.cash)
                    val cash = p0.child("student").child(studentId).child(Keys.cash).value.toString().toDouble()
                    if(product.productPrice>cash){
                        Toast.makeText(con,"u don't have enough money",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        cashPath.setValue(cash - product.productPrice)
                        Toast.makeText(activity!!.baseContext,"added",Toast.LENGTH_SHORT).show()
                        val mealListPath = p0.child("student").child(studentId).child("meals")
                        val currentMeal = p0.child("student").child(studentId).child(Keys.currentMeal)
                        if ((currentMeal.value as String).isNotEmpty()){
                            val mealId = currentMeal.value as String
                            if (mealListPath.child(mealId).exists()){
                                editCurrentMeal(mealListPath.child(mealId),product,mealId)
                            }else{
                                createNewMealWithCurrentId(product,mealId)
                            }
                        }else{
                            createNewMeal(product)
                        }
                    }
                }
            }
        })


    }

    private fun createNewMeal(product:Product){
        val mealId = utils.mealKey(studentId)
        OfflineUser.database.child("student").child(studentId).child(Keys.currentMeal).setValue(mealId)
        val mealPath = database.getReference("student")
            .child(studentId).child("meals").child(mealId)

        mealPath
            .setValue(Meals(
                mealId,
                utils.day(),
                utils.hour(),
                product.productPrice,
                utils.date(),
                "in progress"
            )
        )
        mealPath.child("mealProducts").push().setValue(product)

        OfflineUser.database.child("ToStudentMeal").child(mealId).setValue(studentId)
    }

    private fun createNewMealWithCurrentId(product:Product,mealId:String){
        val mealPath = database.getReference("student")
            .child(studentId).child("meals").child(mealId)

        mealPath
            .setValue(Meals(
                mealId,
                utils.day(),
                utils.hour(),
                product.productPrice,
                utils.date(),
                "in progress"
            )
        )
        mealPath.child("mealProducts").push().setValue(product)
    }

    private fun editCurrentMeal(mealPath:DataSnapshot,product:Product,mealId:String){

        val mealClass = mealPath.getValue(Meals::class.java)!!
        val mealEditValue = OfflineUser.database
            .child("student").child(studentId).child("meals").child(mealId)

        mealEditValue.child(Keys.mealPrice).setValue(mealClass.mealPrice+product.productPrice)
        mealEditValue.child(Keys.mealProducts).push().setValue(product)
    }



















/*

    private fun editCurrentMeal(mealPath:DataSnapshot,product:Product){
        val key = utils.mealKey(studentId)
        val meal = mealPath.getValue(Meals::class.java)!!
        val mealEditValue = OfflineUser.database
            .child("student").child(studentId).child("meals").child(key)

        mealEditValue.child(Keys.mealPrice).setValue(meal.mealPrice+product.productPrice)
        mealEditValue.child(Keys.mealProducts).push().setValue(product)
    }












    private fun createNewMeal(product:Product){
        val key = utils.mealKey(studentId)
        val mealPath = database.getReference("student").child(studentId).child("meals").child(key)
        mealPath
            .setValue(Meals(
                key,
                utils.day(),
                utils.hour(),
                product.productPrice,
                utils.date(),
                "in progress"
            )
            )
        mealPath.child("mealProducts").push().setValue(product)

    }


    * override fun addProduct(barcode: String) {
        database.reference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val products = p0.child("product").child(barcode)
                if(products.exists()){
                    val product = products.getValue(Product::class.java)!!
                    val cashPath = database.reference.child("student").child(studentId).child(key.cash)
                    val cash = p0.child("student").child(studentId).child(key.cash).value.toString().toDouble()
                    if(product.productPrice>cash){
                        Toast.makeText(con,"u don't have enough money",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        cashPath.setValue(cash-product.productPrice)
                        Toast.makeText(activity!!.baseContext,"added",Toast.LENGTH_SHORT).show()
                        val mealListPath = p0.child("student").child(studentId).child("meals")
                        val key = utils.mealKey(studentId)
                        if(mealListPath.exists()){

                            val mealPath = p0.child("student").child(studentId).child(Keys.currentMeal)


                            if (mealPath.exists()){
                                editCurrentMeal(mealPath,product)
                            }
                            else{
                                createNewMeal(product)
                            }
                        }
                        else{
                            createNewMeal(product)
                        }
                    }
                }
            }
        })


    }
    * */



}
