package com.example.app_tv.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.data.DBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private var adminId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLocale("vi")

        setContentView(R.layout.activity_home)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        adminId = sharedPreferences.getInt("ADMIN_ID", -1)

        adminId = intent.getIntExtra("ADMIN_ID", adminId)

        val welcomeTextView = findViewById<TextView>(R.id.textViewWelcome)
        val bookManagementImageView = findViewById<ImageView>(R.id.imageViewBookManagement)
        val memberManagementImageView = findViewById<ImageView>(R.id.imageViewMemberManagement)
        val borrowSlipManagementImageView =
            findViewById<ImageView>(R.id.imageViewBorrowSlipManagement)
        val categoryManagementImageView = findViewById<ImageView>(R.id.imageViewCategoryManagement)
        val supplierManagementImageView = findViewById<ImageView>(R.id.imageViewSupplierManagement)
        val orderImageView = findViewById<ImageView>(R.id.imageViewOrder)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val buttonToday = findViewById<Button>(R.id.buttonToday)


        // Lấy tên admin từ DBHelper và hiển thị
        val dbHelper = DBHelper(this)
        val adminName = dbHelper.getAdminNameById(adminId)

        val welcomeMessage = "Chào mừng, ${adminName ?: "Người dùng"}"
        welcomeTextView.text = welcomeMessage


        // Xử lý sự kiện hình ảnh Quản lý Sách
        bookManagementImageView.setOnClickListener {
            val intent = Intent(this, BookManagementActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện hình ảnh Quản lý Đọc giả
        memberManagementImageView.setOnClickListener {
            val intent = Intent(this, MemberManagementActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện hình ảnh Quản lý Loại Sách
        categoryManagementImageView.setOnClickListener {
            val intent = Intent(this, CategoryManagementActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện hình ảnh Quản lý Nhà cung cấp
        supplierManagementImageView.setOnClickListener {
            val intent = Intent(this, SupplierManagementActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện hình ảnh Quản lý Phiếu mượn
        borrowSlipManagementImageView.setOnClickListener {
            val intent = Intent(this, BorrowSlipManagementActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện hình ảnh Đổi mật khẩu
        orderImageView.setOnClickListener {
            val intent = Intent(this, BookOrderManagementActivity::class.java)
            startActivity(intent)
        }

        // Xử lý sự kiện chọn mục của BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Hiển thị Trang chủ
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
                    val intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
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

        // Xử lý sự kiện nút "Hôm nay"
        buttonToday.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendarView.date = calendar.timeInMillis
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}
