package com.example.app_tv.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.data.DBHelper

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        dbHelper = DBHelper(this)

        val oldPasswordEditText = findViewById<EditText>(R.id.editTextOldPassword)
        val newPasswordEditText = findViewById<EditText>(R.id.editTextNewPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)
        val changePasswordButton = findViewById<Button>(R.id.buttonChangePassword)
        val backButton = findViewById<Button>(R.id.button_back_pass)

        // Lấy ID admin từ SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val adminId = sharedPreferences.getInt("ADMIN_ID", -1)

        changePasswordButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
            } else {
                // Kiểm tra mật khẩu cũ và cập nhật mật khẩu mới
                if (dbHelper.updatePassword(adminId, oldPassword, newPassword)) {
                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                    finish() // Đóng màn hình đổi mật khẩu
                } else {
                    Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show()
                }
            }
        }
        backButton.setOnClickListener { showBackConfirmationDialog() }
    }

    private fun showBackConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn có chắc chắn muốn trở về màn hình chính?")
            .setPositiveButton("Có") { _, _ ->
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            }
            .setNegativeButton("Không", null)
            .create()
            .show()
    }
}
