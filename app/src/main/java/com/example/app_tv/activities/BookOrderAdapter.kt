package com.example.app_tv.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.app_tv.R
import com.example.app_tv.data.models.BookOrder

class BookOrderAdapter(private val context: Context, private val bookOrders: List<BookOrder>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return bookOrders.size
    }

    override fun getItem(position: Int): Any {
        return bookOrders[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_bookorder_item, parent, false)
        val bookOrder = bookOrders[position]

        val tvBookOrderId: TextView = view.findViewById(R.id.tvBookOrderId)
        val tvBookId: TextView = view.findViewById(R.id.tvBookId)
        val tvBookName: TextView = view.findViewById(R.id.tvBookName)
        val tvSupplierId: TextView = view.findViewById(R.id.tvSupplierId)
        val tvSupplierName: TextView = view.findViewById(R.id.tvSupplierName)
        val tvQuantityOrder: TextView = view.findViewById(R.id.tvQuantityOrder)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvIsStocked: TextView = view.findViewById(R.id.tvIsStocked)

        tvBookOrderId.text = "Mã phiếu: ${bookOrder.id.toString()}"
        tvBookId.text = "Mã sách: ${bookOrder.bookId.toString()}"
        tvBookName.text = "Tên sách: Đang cập nhật"
        tvSupplierId.text = "Mã NCC: ${bookOrder.supplierId.toString()}"
        tvSupplierName.text = "Tên NCC: Đang cập nhật"
        tvQuantityOrder.text = "Số lượng nhập: ${bookOrder.quantityOrder.toString()}"
        tvOrderDate.text = bookOrder.orderDate
        tvIsStocked.text =
            Html.fromHtml("Tình trạng: <font color='#FF0000'><b>${if (bookOrder.isStocked) "Đã nhập kho" else "Chưa nhập kho"}</b></font>")

        return view
    }
}
