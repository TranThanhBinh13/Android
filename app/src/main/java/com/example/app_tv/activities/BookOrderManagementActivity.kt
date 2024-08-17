package com.example.app_tv.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.adapters.BookOrderAdapter
import com.example.app_tv.data.DBHelper
import com.example.app_tv.data.models.BookOrder
import java.text.SimpleDateFormat
import java.util.*

class BookOrderManagementActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var lvBookOrders: ListView
    private lateinit var btnBack: Button
    private lateinit var btnFind: Button
    private lateinit var etFindBookOrder: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var etBookOrderId: EditText
    private lateinit var etBookId: EditText
    private lateinit var etBookName: EditText
    private lateinit var etSupplierId: EditText
    private lateinit var etSupplierName: EditText
    private lateinit var etQuantityOrder: EditText
    private lateinit var etOrderDate: EditText
    private lateinit var rgIsStocked: RadioGroup
    private lateinit var radioNotStocked: RadioButton
    private lateinit var radioStocked: RadioButton
    private lateinit var btnABC: Button

    private var bookOrderId: Int = -1
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookorder_management)



        dbHelper = DBHelper(this)
        initializeViews()
        loadBookOrders()

        etBookName.setOnClickListener { showBookSelectionDialog() }
        etSupplierName.setOnClickListener { showSupplierSelectionDialog() }
        btnBack.setOnClickListener { showBackConfirmationDialog() }

        btnFind.setOnClickListener {
            searchBookOrders()
            showLayoutAdd()
        }

        btnAdd.setOnClickListener {
            addBookOrder()
        }

        btnEdit.setOnClickListener {
            editBookOrder()
        }

        btnDelete.setOnClickListener {
            deleteBookOrder()
        }

        btnABC.setOnClickListener {
            clearFields()
            hideLayoutAdd()
        }

        etOrderDate.setOnClickListener {
            showDatePicker()
        }

        lvBookOrders.setOnItemClickListener { _, _, position, _ ->
            val bookOrder = lvBookOrders.adapter.getItem(position) as BookOrder
            populateFields(bookOrder)
            showLayoutAdd()
        }
    }

    private fun loadBookOrders() {
        val bookOrders = dbHelper.getAllBookOrders()
        val adapter = BookOrderAdapter(this, bookOrders)
        lvBookOrders.adapter = adapter
    }

    private fun initializeViews() {
        lvBookOrders = findViewById(R.id.lvBookOrders)
        btnBack = findViewById(R.id.btnBackBookOrder)
        btnFind = findViewById(R.id.btnFindBookOrder)
        etFindBookOrder = findViewById(R.id.etFindBookOrder)
        btnAdd = findViewById(R.id.btnAddBookOrder)
        btnEdit = findViewById(R.id.btnEditBookOrder)
        btnDelete = findViewById(R.id.btnDeleteBookOrder)
        etBookOrderId = findViewById(R.id.etBookOrderId)
        etBookId = findViewById(R.id.etBookId)
        etBookName = findViewById(R.id.etBookName)
        etSupplierId = findViewById(R.id.etSupplierId)
        etSupplierName = findViewById(R.id.etSupplierName)
        etQuantityOrder = findViewById(R.id.etQuantityOrder)
        etOrderDate = findViewById(R.id.etOrderDate)
        rgIsStocked = findViewById(R.id.rgIsStocked)
        radioNotStocked = findViewById(R.id.radioNotStocked)
        radioStocked = findViewById(R.id.radioStocked)
        btnABC = findViewById(R.id.btnABC)

        // Thêm sự kiện thay đổi văn bản cho EditText để tìm kiếm theo thời gian thực
        etFindBookOrder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchBookOrders()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun searchBookOrders() {
        val query = etFindBookOrder.text.toString()
        val bookOrders = dbHelper.searchBookOrders(query)

        // Tạo adapter sử dụng BookOrderAdapter
        val adapter = BookOrderAdapter(this, bookOrders)

        // Đặt adapter cho ListView
        lvBookOrders.adapter = adapter
    }


    private fun addBookOrder() {
        val bookId = etBookId.text.toString().toIntOrNull() ?: return
        val supplierId = etSupplierId.text.toString().toIntOrNull() ?: return
        val quantityOrder = etQuantityOrder.text.toString().toIntOrNull() ?: return
        val orderDate = etOrderDate.text.toString()
        val isStocked = rgIsStocked.checkedRadioButtonId == R.id.radioStocked

        val bookOrder = BookOrder(
            id = 0, // Auto-incremented by database
            bookId = bookId,
            supplierId = supplierId,
            quantityOrder = quantityOrder,
            orderDate = orderDate,
            isStocked = isStocked
        )

        if (dbHelper.addBookOrder(bookOrder)) {
            // Nếu trạng thái là đã nhập kho, cập nhật số lượng tồn
            if (isStocked) {
                dbHelper.updateBookQuantity(bookId, quantityOrder)
            }

            Toast.makeText(this, "Đơn đặt hàng đã được thêm", Toast.LENGTH_SHORT).show()
            loadBookOrders()
            hideLayoutAdd()
        } else {
            Toast.makeText(this, "Thêm đơn đặt hàng thất bại", Toast.LENGTH_SHORT).show()
        }
        clearFields()
    }


    private fun editBookOrder() {
        val id = bookOrderId
        if (id == -1) {
            Toast.makeText(this, "Chọn một đơn đặt hàng để chỉnh sửa", Toast.LENGTH_SHORT).show()
            return
        }

        val bookId = etBookId.text.toString().toIntOrNull() ?: return
        val supplierId = etSupplierId.text.toString().toIntOrNull() ?: return
        val quantityOrder = etQuantityOrder.text.toString().toIntOrNull() ?: return
        val orderDate = etOrderDate.text.toString()
        val isStocked = rgIsStocked.checkedRadioButtonId == R.id.radioStocked

        // Lấy thông tin đơn đặt hàng hiện tại từ cơ sở dữ liệu
        val currentBookOrder = dbHelper.getBookOrderById(id)

        // Cập nhật thông tin đơn đặt hàng
        val bookOrder = BookOrder(
            id = id,
            bookId = bookId,
            supplierId = supplierId,
            quantityOrder = quantityOrder,
            orderDate = orderDate,
            isStocked = isStocked
        )

        if (dbHelper.updateBookOrder(bookOrder)) {
            // Nếu trạng thái của đơn hàng đã thay đổi từ chưa nhập kho sang đã nhập kho
            if (currentBookOrder?.isStocked == false && isStocked) {
                dbHelper.updateBookQuantity(bookId, quantityOrder)
            }
            // Nếu trạng thái của đơn hàng đã thay đổi từ đã nhập kho sang chưa nhập kho
            else if (currentBookOrder?.isStocked == true && !isStocked) {
                dbHelper.updateBookQuantity(bookId, -currentBookOrder.quantityOrder)
            }

            Toast.makeText(this, "Đơn đặt hàng đã được cập nhật", Toast.LENGTH_SHORT).show()
            loadBookOrders()
            hideLayoutAdd()
        } else {
            Toast.makeText(this, "Cập nhật đơn đặt hàng thất bại", Toast.LENGTH_SHORT).show()
        }
        clearFields()
    }


    private fun deleteBookOrder() {
        val id = bookOrderId
        if (id == -1) {
            Toast.makeText(this, "Chọn một đơn đặt hàng để xóa", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Xóa đơn đặt hàng")
            .setMessage("Bạn có chắc chắn muốn xóa đơn đặt hàng này?")
            .setPositiveButton("Xóa") { _, _ ->
                if (dbHelper.deleteBookOrder(id)) {
                    Toast.makeText(this, "Đơn đặt hàng đã được xóa", Toast.LENGTH_SHORT).show()
                    loadBookOrders()
                    clearFields()
                    hideLayoutAdd()
                } else {
                    Toast.makeText(this, "Xóa đơn đặt hàng thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDatePicker() {
        val locale = Locale("vi", "VN")
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                etOrderDate.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun populateFields(bookOrder: BookOrder) {
        bookOrderId = bookOrder.id
        etBookOrderId.setText(bookOrder.id.toString())
        etBookId.setText(bookOrder.bookId.toString())
        etBookName.setText("") // You might want to set the book name based on bookId
        etSupplierId.setText(bookOrder.supplierId.toString())
        etSupplierName.setText("") // You might want to set the supplier name based on supplierId
        etQuantityOrder.setText(bookOrder.quantityOrder.toString())
        etOrderDate.setText(bookOrder.orderDate)
        rgIsStocked.check(if (bookOrder.isStocked) R.id.radioStocked else R.id.radioNotStocked)
    }

    private fun showBookSelectionDialog() {
        val books = dbHelper.getAllBooks()
        val bookNames = books.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Chọn sách")
            .setItems(bookNames) { _, which ->
                val selectedBook = books[which]
                etBookId.setText(selectedBook.id.toString())
                etBookName.setText(selectedBook.name)
            }
            .show()
    }

    private fun showSupplierSelectionDialog() {
        // Lấy danh sách các nhà cung cấp từ cơ sở dữ liệu
        val suppliers = dbHelper.getAllSuppliers()

        // Tạo mảng tên nhà cung cấp để hiển thị trong hộp thoại
        val supplierNames = suppliers.map { it.name }.toTypedArray()

        // Hiển thị hộp thoại chọn nhà cung cấp
        AlertDialog.Builder(this)
            .setTitle("Chọn nhà cung cấp")
            .setItems(supplierNames) { _, which ->
                // Khi người dùng chọn nhà cung cấp, cập nhật các trường nhập liệu
                val selectedSupplier = suppliers[which]
                etSupplierId.setText(selectedSupplier.id.toString())
                etSupplierName.setText(selectedSupplier.name)
            }
            .show()
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

    private fun clearFields() {
        bookOrderId = -1
        etBookOrderId.text.clear()
        etBookId.text.clear()
        etBookName.text.clear()
        etSupplierId.text.clear()
        etSupplierName.text.clear()
        etQuantityOrder.text.clear()
        etOrderDate.text.clear()
        rgIsStocked.clearCheck()
    }

    private fun showLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.VISIBLE
    }

    private fun hideLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.GONE
    }
}
