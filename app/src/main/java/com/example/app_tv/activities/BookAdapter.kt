package com.example.app_tv.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.app_tv.R
import com.example.app_tv.data.models.Book
import java.text.DecimalFormat

class BookAdapter(context: Context, books: List<Book>) :
    ArrayAdapter<Book>(context, R.layout.list_book_item, books) {

    private val decimalFormat = DecimalFormat("###,###")  // Định dạng số nguyên

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_book_item, parent, false)

        val book = getItem(position)

        val imageView = view.findViewById<ImageView>(R.id.book_image)
        val bookIdTextView = view.findViewById<TextView>(R.id.book_id)
        val nameTextView = view.findViewById<TextView>(R.id.book_name)
        val nameCategoryTextView = view.findViewById<TextView>(R.id.book_name_category)
        val priceTextView = view.findViewById<TextView>(R.id.book_price)
        val quantityTextView = view.findViewById<TextView>(R.id.book_quantity)

        book?.let {
            bookIdTextView.text = "Mã sách: ${it.id}"
            nameTextView.text = it.name
            nameCategoryTextView.text =
                "Loại sách: ${it.categoryId}"  // Assuming category ID is a placeholder for category name
            priceTextView.text = "Giá mua: ${decimalFormat.format(it.price)} VND"
            quantityTextView.text = "Số lượng tồn: ${it.quantity}"

            it.imageUri?.let { uri ->
                imageView.setImageURI(Uri.parse(uri))
            } ?: run {
                imageView.setImageResource(R.drawable.ic_book)  // Placeholder image
            }
        }

        return view
    }
}