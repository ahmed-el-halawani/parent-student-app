package com.example.myapplication.controler.market

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.controler.interfaces.ProductListInterface
import com.example.myapplication.module.Product
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.component_market_list_view_tile.view.*

class MarketListView(private val c: Context, private val list:ArrayList<Product>, private val viewState:String, private val interFace: ProductListInterface):BaseAdapter() {
    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val product = list[position]
        val view = when(viewState){
            "L"-> LayoutInflater.from(c).inflate(R.layout.component_market_list_view_tile,null)
            "G"-> LayoutInflater.from(c).inflate(R.layout.component_market_grid_view_tile,null)
            else-> LayoutInflater.from(c).inflate(R.layout.component_market_list_view_tile,null)
        }
        val storage = FirebaseStorage.getInstance().reference
        storage.child(product.productImage).downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(view.iv_productImage)
        }

        view.tv_productName.text = product.productName
        view.tv_productPrice.text = product.productPrice.toString()
        view.tv_productType.text = product.productType
        view.btn_addToBag.text = c.resources.getString(R.string.add_to_bag)
        view.btn_addToBag.background.setTint(ContextCompat.getColor(c,R.color.colorPrimary))

        view.btn_addToBag.setOnClickListener {
            interFace.addProduct(product.productBarcode)
        }

        return view
    }
}