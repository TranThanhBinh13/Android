package com.example.app_tv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.app_tv.R
import com.example.app_tv.data.models.Supplier1

class SupplierAdapter(private val context: Context, private val suppliers: List<Supplier1>) :
    BaseAdapter() {

    override fun getCount(): Int = suppliers.size

    override fun getItem(position: Int): Any = suppliers[position]

    override fun getItemId(position: Int): Long = suppliers[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_supplier_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val supplier = suppliers[position]
        viewHolder.textSupplierId.text = "Mã: ${supplier.id}"
        viewHolder.textSupplierName.text = supplier.name

        return view
    }

    private class ViewHolder(view: View) {
        val textSupplierId: TextView = view.findViewById(R.id.text_supplier_id)
        val textSupplierName: TextView = view.findViewById(R.id.text_supplier_name)
    }
}
