package com.example.app_tv.activities

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.app_tv.R
import com.example.app_tv.data.models.BookBorrowStat

class BookBorrowStatAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val bookBorrowStats: List<BookBorrowStat>
) : ArrayAdapter<BookBorrowStat>(context, layoutResource, bookBorrowStats) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: inflater.inflate(layoutResource, parent, false)

        val bookBorrowStat = bookBorrowStats[position]

        val rankTextView = view.findViewById<TextView>(R.id.rank)
        val bookImageView = view.findViewById<ImageView>(R.id.book_image)
        val bookIdTextView = view.findViewById<TextView>(R.id.book_id)
        val bookNameTextView = view.findViewById<TextView>(R.id.book_name)
        val bookQuantityTextView = view.findViewById<TextView>(R.id.book_quantity)
        val borrowCountTextView = view.findViewById<TextView>(R.id.borrow_count)

        // Set rank
        rankTextView.text = "Top ${position + 1}"

        // Set data to views
        bookBorrowStat.bookImage?.let { uri ->
            bookImageView.setImageURI(Uri.parse(uri))
        } ?: run {
            bookImageView.setImageResource(R.drawable.ic_book)
        }
        bookIdTextView.text = "Mã: ${bookBorrowStat.bookId}"
        bookNameTextView.text = "${bookBorrowStat.bookName}"
        bookQuantityTextView.text = "Có sẵn : ${bookBorrowStat.bookQuantity}"
        borrowCountTextView.text = "Số lần mượn: ${bookBorrowStat.borrowCount}"

        return view
    }
}
