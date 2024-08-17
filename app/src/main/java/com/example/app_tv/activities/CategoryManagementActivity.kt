package com.example.app_tv.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.adapters.CategoryAdapter
import com.example.app_tv.data.DBHelper
import com.example.app_tv.data.models.Category

class CategoryManagementActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var editId: EditText
    private lateinit var editName: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonBack: Button
    private lateinit var buttonABC: Button
    private lateinit var editSearch: EditText
    private lateinit var buttonSearch: Button

    private lateinit var dbHelper: DBHelper
    private var currentCategory: Category? = null
    private lateinit var categories: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_management)

        dbHelper = DBHelper(this)

        // Khởi tạo các view
        listView = findViewById(R.id.list_category)
        editId = findViewById(R.id.edit_category_id)
        editName = findViewById(R.id.edit_category_name)
        buttonAdd = findViewById(R.id.button_add_category)
        buttonEdit = findViewById(R.id.button_edit_category)
        buttonDelete = findViewById(R.id.button_delete_category)
        buttonBack = findViewById(R.id.button_back_category)
        buttonABC = findViewById(R.id.btnABC)
        editSearch = findViewById(R.id.txtFind)
        buttonSearch = findViewById(R.id.btnFind)

        // Tải các danh mục vào ListView
        loadCategories()

        // Đặt sự kiện click cho các mục trong ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            val category = listView.adapter.getItem(position) as Category
            currentCategory = category
            populateCategoryDetails(category)
            showLayoutAdd()

        }

        // Đặt sự kiện click cho các nút
        buttonAdd.setOnClickListener { addCategory() }
        buttonEdit.setOnClickListener { editCategory() }
        buttonDelete.setOnClickListener { deleteCategory() }
        buttonBack.setOnClickListener { showBackConfirmationDialog() }
        buttonABC.setOnClickListener {
            clearInputFields()
            hideLayoutAdd()
        }
        buttonSearch.setOnClickListener {
            searchCategory()
            showLayoutAdd()
        }

        // Thêm sự kiện thay đổi văn bản cho EditText để tìm kiếm theo thời gian thực
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCategory()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadCategories() {
        categories = dbHelper.getAllCategories()
        val adapter = CategoryAdapter(this, categories)
        listView.adapter = adapter
    }

    private fun searchCategory() {
        val query = editSearch.text.toString().trim()
        val filteredCategories = categories.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.id.toString().contains(query)
        }
        updateCategoryList(filteredCategories)
    }

    private fun updateCategoryList(filteredCategories: List<Category>) {
        val adapter = CategoryAdapter(this, filteredCategories)
        listView.adapter = adapter
    }

    private fun populateCategoryDetails(category: Category) {
        editId.setText(category.id.toString())
        editName.setText(category.name)
    }

    private fun addCategory() {
        val name = editName.text.toString()
        if (name.isNotEmpty()) {
            val category = Category(0, name)
            val initialCount = dbHelper.getAllCategories().size
            dbHelper.addCategory(category)
            val newCount = dbHelper.getAllCategories().size

            if (newCount > initialCount) {
                loadCategories()
                clearInputFields()
                Toast.makeText(this, "Thêm loại sách thành công!", Toast.LENGTH_SHORT).show()
                hideLayoutAdd()
            } else {
                Toast.makeText(this, "Thêm loại sách thất bại!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Tên loại sách không được để trống!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editCategory() {
        val id = editId.text.toString().toIntOrNull()
        val name = editName.text.toString()
        if (id != null && name.isNotEmpty()) {
            val category = Category(id, name)
            val initialCategories = dbHelper.getAllCategories()
            dbHelper.updateCategory(category)
            val newCategories = dbHelper.getAllCategories()

            if (initialCategories != newCategories) {
                loadCategories()
                clearInputFields()
                Toast.makeText(this, "Sửa loại sách thành công!", Toast.LENGTH_SHORT).show()
                hideLayoutAdd()
            } else {
                Toast.makeText(this, "Sửa loại sách thất bại!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "ID hoặc tên loại sách không hợp lệ!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteCategory() {
        val id = editId.text.toString().toIntOrNull()
        if (id != null) {
            // Tạo và hiển thị dialog xác nhận
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận xóa")
            builder.setMessage("Bạn có chắc chắn muốn xóa loại sách này không?")
            builder.setPositiveButton("Có") { dialog, which ->
                // Xóa loại sách nếu người dùng chọn "Có"
                val initialCount = dbHelper.getAllCategories().size
                dbHelper.deleteCategory(id)
                val newCount = dbHelper.getAllCategories().size

                if (newCount < initialCount) {
                    loadCategories()
                    clearInputFields()
                    Toast.makeText(this, "Xóa loại sách thành công!", Toast.LENGTH_SHORT).show()
                    hideLayoutAdd()
                } else {
                    Toast.makeText(this, "Xóa loại sách thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Không") { dialog, which ->
                // Đóng dialog nếu người dùng chọn "Không"
                dialog.dismiss()
            }
            builder.show()
        } else {
            Toast.makeText(this, "ID loại sách không hợp lệ!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearInputFields() {
        editId.text.clear()
        editName.text.clear()
        currentCategory = null
    }

    private fun showBackConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn có chắc chắn muốn trở về màn hình chính?")
            .setPositiveButton("Có") { _, _ ->
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .setNegativeButton("Không", null)
            .create()
            .show()
    }

    private fun showLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.VISIBLE
    }

    private fun hideLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.GONE
    }
}
