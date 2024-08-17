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
import com.example.app_tv.adapters.SupplierAdapter
import com.example.app_tv.data.DBHelper
import com.example.app_tv.data.models.Supplier1

class SupplierManagementActivity : AppCompatActivity() {
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
    private var currentSupplier: Supplier1? = null
    private lateinit var suppliers: List<Supplier1>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_management)

        dbHelper = DBHelper(this)

        // Initialize views
        listView = findViewById(R.id.list_supplier)
        editId = findViewById(R.id.edit_supplier_id)
        editName = findViewById(R.id.edit_supplier_name)
        buttonAdd = findViewById(R.id.button_add_supplier)
        buttonEdit = findViewById(R.id.button_edit_supplier)
        buttonDelete = findViewById(R.id.button_delete_supplier)
        buttonBack = findViewById(R.id.button_back_supplier)
        buttonABC = findViewById(R.id.btnABC)
        editSearch = findViewById(R.id.txtFind)
        buttonSearch = findViewById(R.id.btnFind)

        // Load suppliers into ListView
        loadSuppliers()

        // Set ListView item click listener
        listView.setOnItemClickListener { _, _, position, _ ->
            val supplier = listView.adapter.getItem(position) as Supplier1
            currentSupplier = supplier
            populateSupplierDetails(supplier)
            showLayoutAdd()
        }

        // Set button click listeners
        buttonAdd.setOnClickListener { addSupplier() }
        buttonEdit.setOnClickListener { editSupplier() }
        buttonDelete.setOnClickListener { deleteSupplier() }
        buttonBack.setOnClickListener { showBackConfirmationDialog() }
        buttonABC.setOnClickListener {
            clearInputFields()
            hideLayoutAdd()
        }
        buttonSearch.setOnClickListener {
            searchSupplier()
            showLayoutAdd()
        }

        // Add text change listener to EditText for real-time search
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchSupplier()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchSupplier() {
        val query = editSearch.text.toString().trim()
        val filteredSuppliers = suppliers.filter {
            it.name.contains(query, ignoreCase = true) || it.id.toString().contains(query)
        }
        updateSupplierList(filteredSuppliers)
    }

    private fun loadSuppliers() {
        suppliers = dbHelper.getAllSuppliers()  // Lưu trữ danh sách nhà cung cấp
        val adapter = SupplierAdapter(this, suppliers)
        listView.adapter = adapter
    }

    private fun updateSupplierList(filteredSuppliers: List<Supplier1>) {
        val adapter = SupplierAdapter(this, filteredSuppliers)
        listView.adapter = adapter
    }

    private fun populateSupplierDetails(supplier: Supplier1) {
        editId.setText(supplier.id.toString())
        editName.setText(supplier.name)
    }

    private fun addSupplier() {
        val name = editName.text.toString()
        if (name.isNotEmpty()) {
            val supplier = Supplier1(0, name) // ID sẽ được gán bởi cơ sở dữ liệu
            val initialCount = dbHelper.getAllSuppliers().size
            dbHelper.addSupplier(supplier)
            val newCount = dbHelper.getAllSuppliers().size

            if (newCount > initialCount) {
                loadSuppliers()
                clearInputFields()
                Toast.makeText(this, "Thêm nhà cung cấp thành công!", Toast.LENGTH_SHORT).show()
                hideLayoutAdd()
            } else {
                Toast.makeText(this, "Thêm nhà cung cấp thất bại!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Tên nhà cung cấp không được để trống!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editSupplier() {
        val id = editId.text.toString().toIntOrNull()
        val name = editName.text.toString()
        if (id != null && name.isNotEmpty()) {
            val supplier = Supplier1(id, name)
            val initialSuppliers = dbHelper.getAllSuppliers()
            dbHelper.updateSupplier(supplier)
            val newSuppliers = dbHelper.getAllSuppliers()

            if (initialSuppliers != newSuppliers) {
                loadSuppliers()
                clearInputFields()
                Toast.makeText(this, "Sửa nhà cung cấp thành công!", Toast.LENGTH_SHORT).show()
                hideLayoutAdd()
            } else {
                Toast.makeText(this, "Sửa nhà cung cấp thất bại!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "ID hoặc tên nhà cung cấp không hợp lệ!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun deleteSupplier() {
        val id = editId.text.toString().toIntOrNull()
        if (id != null) {
            // Tạo và hiển thị dialog xác nhận
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận xóa")
            builder.setMessage("Bạn có chắc chắn muốn xóa nhà cung cấp này không?")
            builder.setPositiveButton("Có") { dialog, which ->
                // Xóa nhà cung cấp nếu người dùng chọn "Có"
                val initialCount = dbHelper.getAllSuppliers().size
                dbHelper.deleteSupplier(id)
                val newCount = dbHelper.getAllSuppliers().size

                if (newCount < initialCount) {
                    loadSuppliers()
                    clearInputFields()
                    Toast.makeText(this, "Xóa nhà cung cấp thành công!", Toast.LENGTH_SHORT).show()
                    hideLayoutAdd()
                } else {
                    Toast.makeText(this, "Xóa nhà cung cấp thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Không") { dialog, which ->
                // Đóng dialog nếu người dùng chọn "Không"
                dialog.dismiss()
            }
            builder.show()
        } else {
            Toast.makeText(this, "ID nhà cung cấp không hợp lệ!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearInputFields() {
        editId.text.clear()
        editName.text.clear()
        currentSupplier = null
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
