package com.example.app_tv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.app_tv.R
import com.example.app_tv.data.models.Category

class CategoryAdapter(private val context: Context, private val categories: List<Category>) :
    BaseAdapter() {

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Any = categories[position]

    override fun getItemId(position: Int): Long = categories[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_category_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val category = categories[position]
        viewHolder.textCategoryId.text = "Mã: ${category.id}"
        viewHolder.textCategoryName.text = category.name

        return view
    }

    private class ViewHolder(view: View) {
        val textCategoryId: TextView = view.findViewById(R.id.text_category_id)
        val textCategoryName: TextView = view.findViewById(R.id.text_category_name)
    }
}
