package com.example.myapplication.ui.marketUi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.OfflineUser
import com.example.myapplication.controler.market.MarketPagerView
import com.example.myapplication.ui.marketUi.marketFragment.ProductListFragment
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.market_activity_main.*
import com.example.myapplication.ui.marketUi.marketFragment.MyBagFragment

@SuppressLint("SetTextI18n")
class MarketActivity : AppCompatActivity() {
    private lateinit var marketPagesList:ArrayList<Fragment>
    private lateinit var database:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.market_activity_main)
        database = FirebaseDatabase.getInstance().reference
        marketPagesList = arrayListOf(ProductListFragment(), MyBagFragment())

        btn_back2.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onStart() {
        super.onStart()

        VP_marketPager.adapter = MarketPagerView(
            this,
            marketPagesList,
            supportFragmentManager
        )

        val studentCashPath = database.child(Keys.student).child(OfflineUser.studentId).child(Keys.cash)
        studentCashPath.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val cash = p0.value as Long
                tv_studentCashView.text = "$cash EGP"
            }
        })
    }
}
