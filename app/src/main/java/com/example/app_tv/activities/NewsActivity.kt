package com.example.app_tv.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_tv.R
import com.example.app_tv.adapters.NewsAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    // Định nghĩa danh sách các bài viết với tiêu đề và URL
    private val newsList = listOf(
        Pair(
            "Khởi công xây dựng sửa chữa thư viện tại cơ sở Long Trường",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/news-ufm-khoi-cong-xay-dung-sua-chua-thu-vien-tai-co-so-long-truong"
        ),
        Pair(
            "Hợp tác chia sẻ nguồn lực thông tin",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/hop-tac-chia-se-nguon-luc-thong-tin-xu-huong-moi-cho-thu-vien-hoc-thuat-trong-ky-nguyen-so"
        ),
        Pair(
            "Hội thảo về công nghệ mới",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/hoi-thao-ve-cong-nghe-moi-noi-va-quan-ly-thu-vien-so"
        ),
        Pair(
            "Cơ hội mới cho người học trường Đại học Tài chính - Marketing và trường Đại học Luật TP.HCM ký kết hợp tác MOU",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/co-hoi-moi-cho-nguoi-hoc-truong-dai-hoc-tai-chinh-marketing-va-truong-dai-hoc-luat-tp-hcm-ky-ket-hop-tac-mou"
        ),
        Pair(
            "Trường Đại học Tài chính - Marketing và trường Đại học Ngân hàng TP.HCM ký kết hợp tác giai đoạn 2024-2025",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/truong-dai-hoc-tai-chinh-marketing-va-truong-dai-hoc-ngan-hang-tp-hcm-ky-ket-hop-tac-giai-doan-2024-2025"
        ),
        Pair(
            "Trường Đại học Tài chính - Marketing và trường Đại học Sài Gòn ký kết hợp tác giai đoạn 2024-2025",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/truong-dai-hoc-tai-chinh-marketing-va-truong-dai-hoc-sai-gon-ky-ket-hop-tac-giai-doan-2024-2025"
        ),
        Pair(
            "Tuổi trẻ UFM thực hiện không gian đọc xanh tại thư viện trường",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/tuoi-tre-ufm-thuc-hien-khong-gian-doc-xanh-tai-thu-vien-truong"
        ),
        Pair(
            "Tập huấn số hóa tài liệu thư viện thực hiện chuyển đổi số",
            "https://thuvien.ufm.edu.vn/vi/tin-tuc-su-kien/tap-huan-so-hoa-tai-lieu-thu-vien-thuc-hien-chuyen-doi-so"
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        recyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(newsList) { url ->
            val intent = Intent(this, NewsDetailActivity::class.java).apply {
                putExtra(NewsDetailActivity.EXTRA_URL, url) // Gửi URL cho NewsDetailActivity
            }
            startActivity(intent)
        }
        recyclerView.adapter = newsAdapter

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Hiển thị Trang chủ
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_report -> {
                    // Mở Tin tức Activity
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_news -> {
                    // Mở Tin tức Activity
                    true
                }

                R.id.nav_profile -> {
                    // Mở Cá nhân Activity
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}
