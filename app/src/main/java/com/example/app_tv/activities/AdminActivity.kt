package com.example.app_tv.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.app_tv.R
import com.example.app_tv.data.DBHelper
import com.example.app_tv.data.models.Admin
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.Toast

class AdminActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var admin: Admin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        dbHelper = DBHelper(this)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val adminId = sharedPreferences.getInt("ADMIN_ID", -1)

        if (adminId != -1) {
            admin = getAdminInfo(adminId)
        } else {
            // Xử lý trường hợp không nhận được adminId
            Toast.makeText(this, "Không tìm thấy thông tin admin", Toast.LENGTH_SHORT).show()
            finish() // Đóng activity nếu không có adminId hợp lệ
        }

        displayAdminInfo()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        fab.setOnClickListener { view ->
            showPopupMenu(view)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_report -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_news -> {
                    val intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_profile -> {
                    true
                }

                else -> false
            }
        }
    }

    private fun getAdminInfo(adminId: Int): Admin {
        return dbHelper.getAdminById(adminId)
    }

    private fun displayAdminInfo() {
        val idTextView: TextView = findViewById(R.id.textViewidadmin)
        val nameTextView: TextView = findViewById(R.id.textViewnameadmin)
        val emailTextView: TextView = findViewById(R.id.textViewemailadmin)
        val usernameTextView: TextView = findViewById(R.id.textViewusernameadmin)
        val passwordTextView: TextView = findViewById(R.id.textViewpasswordadmin)

        idTextView.text = "Id: ${admin.id}"
        nameTextView.text = "Tên tài khoản: ${admin.name}"
        emailTextView.text = "Email tài khoản: ${admin.email}"
        usernameTextView.text = "Tên tài khoản: ${admin.username}"
        passwordTextView.text = "Mật khẩu: ${admin.password}"
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.popup_menu)

        val adminName = admin.name

        val menuInfoItem = popupMenu.menu.findItem(R.id.menu_info)
        menuInfoItem.title = "${adminName}"
        menuInfoItem.isEnabled = false

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_change_password -> {
                    startActivity(Intent(this, ChangePasswordActivity::class.java))
                    true
                }

                R.id.menu_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận đăng xuất")
            .setMessage("Bạn có chắc chắn muốn thoát không?")
            .setPositiveButton("Có") { _, _ ->
                logout()
            }
            .setNegativeButton("Không", null)
            .show()
    }

    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
