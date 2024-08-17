package com.example.app_tv.activities

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.adapters.BookAdapter
import com.example.app_tv.data.DBHelper
import com.example.app_tv.data.models.Book

class BookManagementActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var editBookId: EditText
    private lateinit var editName: EditText
    private lateinit var editPrice: EditText
    private lateinit var editCategoryId: EditText
    private lateinit var editQuantity: EditText

    private lateinit var imageView: ImageView
    private lateinit var buttonAdd: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonBack: Button
    private lateinit var buttonABC: Button
    private lateinit var txtSearchBook: EditText
    private lateinit var btnSearchBook: Button

    private lateinit var dbHelper: DBHelper
    private var currentBook: Book? = null
    private var imageUri: Uri? = null
    private var currentImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_management)

        dbHelper = DBHelper(this)

        // Initialize views
        listView = findViewById(R.id.book_list_view)
        editBookId = findViewById(R.id.edit_book_id)
        editName = findViewById(R.id.edit_name)
        editPrice = findViewById(R.id.edit_price)
        editCategoryId = findViewById(R.id.edit_category_id)
        editQuantity = findViewById(R.id.edit_quantity)
        imageView = findViewById(R.id.image_view)
        buttonAdd = findViewById(R.id.button_add)
        buttonEdit = findViewById(R.id.button_edit)
        buttonDelete = findViewById(R.id.button_delete)
        buttonBack = findViewById(R.id.button_back)
        buttonABC = findViewById(R.id.btnABC)
        txtSearchBook = findViewById(R.id.txtFind)
        btnSearchBook = findViewById(R.id.btnFind)

        // Load books into ListView
        loadBooks()

        // Set ListView item click listener
        listView.setOnItemClickListener { _, _, position, _ ->
            val book = listView.adapter.getItem(position) as Book
            currentBook = book
            populateBookDetails(book)
            showLayoutAdd()
        }

        // Set button click listeners
        buttonAdd.setOnClickListener { addBook() }
        buttonEdit.setOnClickListener { editBook() }
        buttonDelete.setOnClickListener { deleteBook() }
        buttonBack.setOnClickListener { showBackConfirmationDialog() }
        imageView.setOnClickListener { openImageChooser() }
        editCategoryId.setOnClickListener { showCategorySelectionDialog() }
        buttonABC.setOnClickListener {
            clearFields()
            hideLayoutAdd()
        }
        btnSearchBook.setOnClickListener {
            searchBook()
            showLayoutAdd()
        }

        // Add text change listener to txtSearchBook
        txtSearchBook.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchBook()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun searchBook() {
        val query = txtSearchBook.text.toString()
        if (query.isNotBlank()) {
            val books = dbHelper.searchBooks(query)
            val adapter = BookAdapter(this, books)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()  // Cập nhật adapter
        } else {
            loadBooks()
        }
    }


    private fun showCategorySelectionDialog() {
        // Lấy danh sách các loại sách từ cơ sở dữ liệu
        val categories = dbHelper.getAllCategories()

        // Chuyển danh sách các loại sách thành một mảng tên để hiển thị
        val categoryNames = categories.map { it.name }.toTypedArray()

        // Tạo AlertDialog với danh sách các loại sách
        AlertDialog.Builder(this)
            .setTitle("Chọn mã loại sách")
            .setItems(categoryNames) { dialog, which ->
                // Xử lý sự kiện chọn
                val selectedCategory = categories[which]
                editCategoryId.setText(selectedCategory.id.toString())
            }
            .show()
    }

    private fun loadBooks() {
        val books = dbHelper.getAllBooks()
        val adapter = BookAdapter(this, books)
        listView.adapter = adapter
    }

    private fun populateBookDetails(book: Book) {
        editBookId.setText(book.id.toString())
        editName.setText(book.name)
        editPrice.setText(book.price.toString())
        editCategoryId.setText(book.categoryId.toString())
        editQuantity.setText(book.quantity.toString())  // Hiển thị số lượng
        if (book.imageUri != null) {
            imageView.setImageURI(Uri.parse(book.imageUri))
            currentImageUri = book.imageUri
        } else {
            imageView.setImageResource(R.drawable.ic_book)
            currentImageUri = null
        }
    }


    private fun addBook() {
        val idStr = editBookId.text.toString()
        val name = editName.text.toString()
        val priceStr = editPrice.text.toString()
        val categoryIdStr = editCategoryId.text.toString()
        val quantityStr = editQuantity.text.toString() // Số lượng

        val id = idStr.toIntOrNull()
        val price = priceStr.toDoubleOrNull()
        val categoryId = categoryIdStr.toIntOrNull()
        val quantity = quantityStr.toIntOrNull() // Số lượng

        if (name.isNotBlank() && price != null && categoryId != null && quantity != null) {
            val newBook = Book(id ?: 0, name, imageUri?.toString(), categoryId, quantity, price)
            dbHelper.addBook(newBook)
            loadBooks()
            clearFields()
            showToast("Thêm sách thành công")
            hideLayoutAdd() // Ẩn layoutadd sau khi thêm
        } else {
            showToast("Vui lòng điền đầy đủ thông tin sách")
        }
    }

    private fun editBook() {
        val name = editName.text.toString()
        val priceStr = editPrice.text.toString()
        val categoryIdStr = editCategoryId.text.toString()
        val quantityStr = editQuantity.text.toString() // Số lượng

        val price = priceStr.toDoubleOrNull()
        val categoryId = categoryIdStr.toIntOrNull()
        val quantity = quantityStr.toIntOrNull() // Số lượng

        currentBook?.let {
            if (name.isNotBlank() && price != null && categoryId != null && quantity != null) {
                val updatedBook = Book(it.id, name, currentImageUri, categoryId, quantity, price)
                dbHelper.updateBook(updatedBook)
                loadBooks()
                clearFields()
                showToast("Cập nhật sách thành công")
                hideLayoutAdd() // Ẩn layoutadd sau khi sửa
            } else {
                showToast("Vui lòng điền đầy đủ thông tin sách")
            }
        } ?: showToast("Chưa chọn sách để chỉnh sửa")
    }

    private fun deleteBook() {
        currentBook?.let {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận xóa")
            builder.setMessage("Bạn có chắc chắn muốn xóa sách này không?")
            builder.setPositiveButton("Có") { dialog, which ->
                dbHelper.deleteBook(it.id)
                loadBooks()
                clearFields()
                showToast("Xóa sách thành công")
                hideLayoutAdd() // Ẩn layoutadd sau khi xóa
            }
            builder.setNegativeButton("Không") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        } ?: showToast("Chưa chọn sách để xóa")
    }

    private fun hideLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.GONE
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    private fun openImageChooser() {
        // Lấy tên ảnh từ strings.xml
        val imageNames = resources.getStringArray(R.array.image_names_book)

        // Chuyển đổi tên ảnh thành các resource IDs
        val images = imageNames.map { imageName ->
            val resourceId = resources.getIdentifier(imageName, "drawable", packageName)
            if (resourceId == 0) {
                throw Resources.NotFoundException("Resource not found for imageName: $imageName")
            }
            resourceId
        }.toTypedArray()

        // Hiển thị dialog để người dùng chọn ảnh
        AlertDialog.Builder(this)
            .setTitle("Chọn hình ảnh")
            .setItems(images.map { resources.getResourceEntryName(it) }
                .toTypedArray()) { _, which ->
                imageUri = Uri.parse("android.resource://${packageName}/${images[which]}")
                imageView.setImageURI(imageUri)
                currentImageUri = imageUri?.toString() // Cập nhật currentImageUri
            }
            .show()
    }


    private fun clearFields() {
        editBookId.text.clear()
        editName.text.clear()
        editPrice.text.clear()
        editCategoryId.text.clear()
        editQuantity.text.clear()  // Xóa số lượng
        imageView.setImageResource(R.drawable.ic_book)
        currentBook = null
        currentImageUri = null
    }

    private fun showLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.VISIBLE
    }
}