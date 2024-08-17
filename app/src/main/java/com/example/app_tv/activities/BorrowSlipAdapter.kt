package com.example.app_tv.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.app_tv.R
import com.example.app_tv.activities.BorrowSlipManagementActivity
import com.example.app_tv.data.models.BorrowSlip

class BorrowSlipAdapter(private val context: Context, private val borrowSlips: List<BorrowSlip>) :
    BaseAdapter() {

    override fun getCount(): Int = borrowSlips.size

    override fun getItem(position: Int): Any = borrowSlips[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_borrow_slip_item, parent, false)

        val borrowSlip = borrowSlips[position]

        val borrowSlipId = view.findViewById<TextView>(R.id.borrow_slip_id)
        val memberId = view.findViewById<TextView>(R.id.member_id)
        val memberName = view.findViewById<TextView>(R.id.member_name)
        val bookId = view.findViewById<TextView>(R.id.book_id)
        val bookName = view.findViewById<TextView>(R.id.book_name)
        val borrowDate = view.findViewById<TextView>(R.id.borrow_date)
        val returnDate = view.findViewById<TextView>(R.id.return_date)
        val status = view.findViewById<TextView>(R.id.status)



        borrowSlipId.text = "Mã phiếu: ${borrowSlip.id}"
        memberId.text = "Mã độc giả: ${borrowSlip.memberId}"
        memberName.text = "Tên độc giả: Đang cập nhật"
        bookId.text = "Mã sách: ${borrowSlip.bookId}"
        bookName.text = "Tên sách: Đang cập nhật"
        borrowDate.text = "Ngày mượn: ${borrowSlip.borrowDate}"
        returnDate.text = "Ngày trả: ${borrowSlip.returnDate}"
        status.text =
            Html.fromHtml("Tình trạng: <font color='#FF0000'><b>${borrowSlip.status}</b></font>")

        return view
    }
}
