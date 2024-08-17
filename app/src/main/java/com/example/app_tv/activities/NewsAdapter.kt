package com.example.app_tv.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_tv.R

class NewsAdapter(
    private val newsList: List<Pair<String, String>>,
    private val onItemClick: (String) -> Unit // Sửa đổi để chỉ truyền URL
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val summaryTextView: TextView = itemView.findViewById(R.id.textViewSummary)
        private val cardView: CardView =
            itemView.findViewById(R.id.cardView) // Đảm bảo ID chính xác

        init {
            cardView.setOnClickListener {
                val (_, url) = newsList[adapterPosition] // Chỉ lấy URL
                onItemClick(url)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val (title, _) = newsList[position]
        holder.titleTextView.text = title
        holder.summaryTextView.text = ""
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}
