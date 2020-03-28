package com.example.myapplication.controler.parent

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.myapplication.R
import com.example.myapplication.controler.Keys
import com.example.myapplication.controler.interfaces.ListViewInterFaces
import com.example.myapplication.module.Student
import com.example.myapplication.ui.parentUi.StudentActivity
import kotlinx.android.synthetic.main.component_parent_child_tile.view.*

class StudentListView(private val c:Context, private val student:ArrayList<Student>, private val listViewInterFace: ListViewInterFaces):BaseAdapter() {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val student = student[position]
        val view = LayoutInflater.from(c).inflate(R.layout.component_parent_child_tile,null)
        view.name.text = student.name

        view.btn_more.setOnClickListener{
            listViewInterFace.showHideButton(view)
        }

        view.btn_remove.setOnClickListener {
            listViewInterFace.removeStudent(student.id)
        }

        view.setOnClickListener {
            val intent = Intent(c,StudentActivity::class.java)
            intent.putExtra(Keys.studentId,student.id)
            c.startActivity(intent)
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return student[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return student.size
    }

}