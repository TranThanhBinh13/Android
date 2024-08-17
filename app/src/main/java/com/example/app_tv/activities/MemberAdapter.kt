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
import com.example.app_tv.data.models.Member

class MemberAdapter(context: Context, members: List<Member>) :
    ArrayAdapter<Member>(context, R.layout.list_member_item, members) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_member_item, parent, false)

        val member = getItem(position)

        val imageView = view.findViewById<ImageView>(R.id.image_avt)
        val memberIdTextView = view.findViewById<TextView>(R.id.member_id)
        val nameTextView = view.findViewById<TextView>(R.id.hoten)
        val emailTextView = view.findViewById<TextView>(R.id.email)

        member?.let {
            memberIdTextView.text = "Mã đọc giả: " + it.id.toString()// Displaying member ID
            nameTextView.text = it.name
            emailTextView.text = it.email
            it.imageUri?.let { uri ->
                imageView.setImageURI(Uri.parse(uri))
            } ?: run {
                imageView.setImageResource(R.drawable.ic_avt) // Placeholder image
            }
        }

        return view
    }
}
