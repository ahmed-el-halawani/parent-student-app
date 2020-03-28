package com.example.myapplication.controler.productsAndMeals

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.controler.interfaces.MealAdaptorInterface
import com.example.myapplication.module.Product
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.component_market_list_view_tile.view.*


class MealAdaptor(private val c:Context,private val list:ArrayList<Pair<String,Product>>,val interFace: MealAdaptorInterface):BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val product = list[position].second
        val view = LayoutInflater.from(c).inflate(R.layout.component_market_list_view_tile,parent,false)
        val storage = FirebaseStorage.getInstance().reference
        storage.child(product.productImage).downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(view.iv_productImage)
        }
        view.tv_productName.text = product.productName
        view.tv_productPrice.text = product.productPrice.toString()
        view.tv_productType.text = product.productType
        view.btn_addToBag.text = c.resources.getString(R.string.remove)
        view.btn_addToBag.background.setTint(ContextCompat.getColor(c,R.color.red))


        view.btn_addToBag.setOnClickListener {
            interFace.removeProduct(list[position].first)
        }


        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}