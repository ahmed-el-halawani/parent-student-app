package com.example.myapplication.controler.parent

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.R

private val pageTitle = arrayListOf(
    R.string.control,
    R.string.history
)

class ParentStudentViewPager(private val c: Context, private val list:ArrayList<Fragment>, fm:FragmentManager):FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return c.resources.getString(pageTitle[position])
    }

    override fun getCount(): Int {
        return list.size
    }
}