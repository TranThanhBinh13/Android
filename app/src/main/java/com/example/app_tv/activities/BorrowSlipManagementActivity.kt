package com.example.app_tv.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.adapters.BorrowSlipAdapter
import com.example.app_tv.data.DBHelper
import android.widget.LinearLayout
import com.example.app_tv.data.models.BorrowSlip
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class BorrowSlipManagementActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var etBorrowSlipId: EditText
    private lateinit var etMemberId: EditText
    private lateinit var etMemberName: EditText
    private lateinit var etBookId: EditText
    private lateinit var etBookName: EditText
    private lateinit var etBorrowDate: EditText
    private lateinit var etReturnDate: EditText
    private lateinit var txtDate: TextView
    private lateinit var rbTinhtrang: RadioGroup
    private lateinit var btnAddBorrowSlip: Button
    private lateinit var btnEditBorrowSlip: Button
    private lateinit var btnDeleteBorrowSlip: Button
    private lateinit var btnBackBorrowSlip: Button
    private lateinit var lvBorrowSlips: ListView
    private lateinit var borrowSlipAdapter: BorrowSlipAdapter
    private lateinit var borrowSlipList: MutableList<BorrowSlip>
    private lateinit var buttonABC: Button
    private lateinit var buttonMail: Button
    private lateinit var editSearch: EditText
    private lateinit var buttonSearch: Button

    private var currentBorrowSlip: BorrowSlip? = null
    private lateinit var borrowslips: List<BorrowSlip>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_slip_management)

        dbHelper = DBHelper(this)
        etBorrowSlipId = findViewById(R.id.etBorrowSlipId)
        etMemberId = findViewById(R.id.etMemberId)
        etMemberName = findViewById(R.id.etMemberName)
        etBookId = findViewById(R.id.etBookId)
        etBookName = findViewById(R.id.etBookName)
        etBorrowDate = findViewById(R.id.etBorrowDate)
        etReturnDate = findViewById(R.id.etReturnDate)
        rbTinhtrang = findViewById(R.id.rbTinhtrang)
        btnAddBorrowSlip = findViewById(R.id.btnAddBorrowSlip)
        btnEditBorrowSlip = findViewById(R.id.btnEditBorrowSlip)
        btnDeleteBorrowSlip = findViewById(R.id.btnDeleteBorrowSlip)
        btnBackBorrowSlip = findViewById(R.id.btnBackBorrowSlip)
        lvBorrowSlips = findViewById(R.id.lvBorrowSlips)
        buttonABC = findViewById(R.id.btnABC)
        txtDate = findViewById(R.id.txtDate)
        buttonMail = findViewById(R.id.buttonMail)
        editSearch = findViewById(R.id.txtFind)
        buttonSearch = findViewById(R.id.btnFind)



        borrowSlipList = mutableListOf()
        borrowSlipAdapter = BorrowSlipAdapter(this, borrowSlipList)
        lvBorrowSlips.adapter = borrowSlipAdapter

        loadBorrowSlips()

        btnAddBorrowSlip.setOnClickListener { addBorrowSlip() }
        btnEditBorrowSlip.setOnClickListener { editBorrowSlip() }
        btnDeleteBorrowSlip.setOnClickListener { deleteBorrowSlip() }
        btnBackBorrowSlip.setOnClickListener { showBackConfirmationDialog() }

        setupDatePickers()
        setupListViewClickListener()
        etMemberName.setOnClickListener { showMemberSelectionDialog() }
        etBookName.setOnClickListener { showBookSelectionDialog() }
        buttonABC.setOnClickListener {
            clearFields()
            hideLayoutAdd()
        }
        buttonMail.setOnClickListener { sendMail() }
        buttonSearch.setOnClickListener { showLayoutAdd() }


        // Thêm sự kiện thay đổi văn bản cho EditText để tìm kiếm theo thời gian thực
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchBorrowslip()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // Thêm sự kiện cho rbTinhtrang
        rbTinhtrang.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioChuatra -> {
                    // Khi chọn "Chưa trả sách"
                    etReturnDate.isEnabled = false // Khóa không cho phép nhập ngày trả sách
                    etReturnDate.text.clear() // Xóa nội dung của trường ngày trả sách
                }

                R.id.radioDatra -> {
                    // Khi chọn "Đã trả sách"
                    etReturnDate.isEnabled = true // Cho phép nhập ngày trả sách
                }
            }
        }

        // Hiển thị ngày hiện tại
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        txtDate.text = currentDate.format(formatter)

        etBorrowDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkBorrowDateAndUpdateButtonColor()
            }
        })

        checkBorrowDateAndUpdateButtonColor()

    }


    private fun sendMail() {
        val borrowSlipId = etBorrowSlipId.text.toString().toIntOrNull()
        if (borrowSlipId == null) {
            Toast.makeText(this, "Mã phiếu mượn không hợp lệ.", Toast.LENGTH_SHORT).show()
            return
        }

        val borrowSlip = dbHelper.getBorrowSlipById(borrowSlipId)
        if (borrowSlip == null) {
            Toast.makeText(this, "Phiếu mượn không tìm thấy.", Toast.LENGTH_SHORT).show()
            return
        }

        val member = dbHelper.getMemberById(borrowSlip.memberId)
        if (member == null) {
            Toast.makeText(this, "Thành viên không tìm thấy.", Toast.LENGTH_SHORT).show()
            return
        }

        val email = member.email
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Email độc giả không hợp lệ.", Toast.LENGTH_SHORT).show()
            return
        }

        val subject = "Thông báo mượn sách"
        val message = """
    Chào ${member.name},
      
    Chúng tôi xin trân trọng thông báo rằng bạn đã thực hiện việc mượn sách từ thư viện trường Đại học Tài chính Marketing vào ngày ${borrowSlip.borrowDate}. Việc mượn sách này là một phần quan trọng trong quá trình học tập và nghiên cứu của bạn, và chúng tôi mong muốn đảm bảo rằng các tài liệu được duy trì và sử dụng hiệu quả.

    Chúng tôi kính đề nghị bạn vui lòng trả lại sách trong vòng 2 ngày kể từ ngày nhận được thông báo này. Việc trả sách đúng hạn không chỉ giúp bạn tuân thủ các quy định của thư viện mà còn hỗ trợ các sinh viên khác có thể tiếp cận tài liệu cần thiết cho học tập của họ.

    Nếu bạn gặp bất kỳ vấn đề nào trong việc trả sách hoặc có bất kỳ câu hỏi nào về quy trình, xin vui lòng liên hệ với bộ phận hỗ trợ của thư viện qua email hoặc số điện thoại được cung cấp trong hệ thống thư viện.

    Chúng tôi rất cảm kích sự hợp tác của bạn và hy vọng rằng bạn sẽ tiếp tục sử dụng các dịch vụ của thư viện một cách hiệu quả.

    Trân trọng,
    Thư viện Trường Đại học Tài chính Marketing
    
    Hotline: 036 9270 849
    
    
""".trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // Đặt loại là text/plain để phù hợp với nhiều ứng dụng
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Chia sẻ qua:"))
        } else {
            Toast.makeText(this, "Không thể gửi", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupDatePickers() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val localeVN = Locale("vi", "VN")
        Locale.setDefault(localeVN)

        etBorrowDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    etBorrowDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setLocale(this, localeVN)
            }.show()
        }

        etReturnDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    etReturnDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setLocale(this, localeVN)
            }.show()
        }
    }

    private fun setLocale(datePickerDialog: DatePickerDialog, locale: Locale) {
        val datePicker = datePickerDialog.datePicker
        try {
            val f = datePicker.javaClass.getDeclaredField("mDelegate")
            f.isAccessible = true
            val delegate = f.get(datePicker)
            val cdpClass = Class.forName("com.android.internal.widget.DatePickerCalendarDelegate")
            if (delegate != null && delegate.javaClass != cdpClass) {
                f.set(datePicker, null)
                datePicker.removeAllViews()
                val datePickerConstructor = cdpClass.getConstructor(
                    DatePicker::class.java,
                    Context::class.java,
                    AttributeSet::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                datePickerConstructor.isAccessible = true
                val datePickerDelegate = datePickerConstructor.newInstance(
                    datePicker,
                    datePicker.context,
                    null,
                    android.R.attr.datePickerStyle,
                    0
                )
                f.set(datePicker, datePickerDelegate)
                datePickerDelegate.javaClass.getDeclaredMethod("setLocale", Locale::class.java)
                    .invoke(datePickerDelegate, locale)
                datePicker.updateDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkBorrowDateAndUpdateButtonColor() {
        val borrowDateStr = etBorrowDate.text.toString()
        val borrowDate = try {
            LocalDate.parse(borrowDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            // Nếu không thể phân tích ngày, quay lại màu xanh mặc định
            buttonMail.setBackgroundColor(Color.GREEN)
            return
        }

        val currentDate = LocalDate.now()
        val daysDifference = ChronoUnit.DAYS.between(borrowDate, currentDate)

        val isReturned = rbTinhtrang.checkedRadioButtonId == R.id.radioDatra

        if (daysDifference > 7 && !isReturned) {
            buttonMail.setBackgroundColor(Color.RED)
        } else {
            buttonMail.setBackgroundColor(Color.GREEN)
        }
    }

    private fun setupListViewClickListener() {
        lvBorrowSlips.setOnItemClickListener { _, _, position, _ ->
            val selectedBorrowSlip = borrowSlipList[position]
            etBorrowSlipId.setText(selectedBorrowSlip.id.toString())
            etMemberId.setText(selectedBorrowSlip.memberId.toString())
            etBookId.setText(selectedBorrowSlip.bookId.toString())
            etBorrowDate.setText(selectedBorrowSlip.borrowDate)
            etReturnDate.setText(selectedBorrowSlip.returnDate)
            when (selectedBorrowSlip.status) {
                "Chưa trả sách" -> rbTinhtrang.check(R.id.radioChuatra)
                "Đã trả sách" -> rbTinhtrang.check(R.id.radioDatra)
            }
            currentBorrowSlip = selectedBorrowSlip // Cập nhật biến currentBorrowSlip
            showLayoutAdd()
        }
    }


    private fun loadBorrowSlips() {
        borrowslips = dbHelper.getAllBorrowSlips()
        borrowSlipList.clear()
        borrowSlipList.addAll(borrowslips)
        borrowSlipAdapter.notifyDataSetChanged()
    }

    private fun searchBorrowslip() {
        val query = editSearch.text.toString().trim()
        borrowslips = dbHelper.getAllBorrowSlips() // Cập nhật danh sách đầy đủ
        val filteredBorrowSlips = borrowslips.filter {
            it.id.toString().contains(query) ||
                    it.memberId.toString().contains(query) ||
                    it.bookId.toString().contains(query)
        }
        updateBorrowSlipList(filteredBorrowSlips)
    }


    private fun updateBorrowSlipList(filteredBorrowSlips: List<BorrowSlip>) {
        borrowSlipList.clear()
        borrowSlipList.addAll(filteredBorrowSlips)
        borrowSlipAdapter.notifyDataSetChanged()
    }


    private fun addBorrowSlip() {
        val memberId = etMemberId.text.toString().toIntOrNull()
        val bookId = etBookId.text.toString().toIntOrNull()
        val borrowDate = etBorrowDate.text.toString()
        val returnDate = etReturnDate.text.toString()
        val status =
            if (rbTinhtrang.checkedRadioButtonId == R.id.radioChuatra) "Chưa trả sách" else "Đã trả sách"

        if (memberId == null || bookId == null || borrowDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra ngày mượn hợp lệ
        val borrowDateParsed = try {
            LocalDate.parse(borrowDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            Toast.makeText(this, "Ngày mượn không hợp lệ.", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra ngày trả sách nếu có
        val returnDateParsed = if (returnDate.isNotEmpty()) {
            try {
                LocalDate.parse(returnDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                Toast.makeText(this, "Ngày trả không hợp lệ.", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            null
        }

        // Kiểm tra ngày trả sách không được nhỏ hơn ngày mượn
        if (returnDateParsed != null && returnDateParsed.isBefore(borrowDateParsed)) {
            Toast.makeText(
                this,
                "Ngày trả sách không thể trước ngày mượn sách.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val currentQuantity = dbHelper.getBookQuantity(bookId)
        if (currentQuantity <= 0) {
            Toast.makeText(this, "Sách không đủ số lượng để mượn.", Toast.LENGTH_SHORT).show()
            return
        }

        val borrowSlip = BorrowSlip(
            id = 0, // id sẽ được tự động sinh ra bởi cơ sở dữ liệu
            memberId = memberId,
            bookId = bookId,
            borrowDate = borrowDate,
            returnDate = returnDate,
            status = status
        )

        // Thêm phiếu mượn vào cơ sở dữ liệu
        try {
            dbHelper.addBorrowSlip(borrowSlip)
            dbHelper.updateBookQuantity(bookId, -1) // Giảm số lượng tồn sách đi 1
            loadBorrowSlips() // Làm mới danh sách phiếu mượn
            clearFields() // Xóa các trường nhập liệu
            Toast.makeText(this, "Phiếu mượn được thêm thành công.", Toast.LENGTH_SHORT).show()
            hideLayoutAdd()
        } catch (e: Exception) {
            Toast.makeText(this, "Đã xảy ra lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun editBorrowSlip() {
        val id = etBorrowSlipId.text.toString().toIntOrNull()
        val memberId = etMemberId.text.toString().toIntOrNull()
        val bookId = etBookId.text.toString().toIntOrNull()
        val borrowDate = etBorrowDate.text.toString()

        // Xử lý giá trị ngày trả sách dựa trên trạng thái của radio button
        val returnDate = if (rbTinhtrang.checkedRadioButtonId == R.id.radioChuatra) {
            null // Nếu trạng thái là "Chưa trả sách", đặt returnDate thành null
        } else {
            etReturnDate.text.toString().takeIf { it.isNotEmpty() }
        }

        val status = when (rbTinhtrang.checkedRadioButtonId) {
            R.id.radioChuatra -> "Chưa trả sách"
            R.id.radioDatra -> "Đã trả sách"
            else -> ""
        }

        // Kiểm tra dữ liệu hợp lệ
        if (id == null || memberId == null || bookId == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra ngày mượn và ngày trả sách
        val borrowDateParsed = try {
            LocalDate.parse(borrowDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            Toast.makeText(this, "Ngày mượn không hợp lệ.", Toast.LENGTH_SHORT).show()
            return
        }

        val returnDateParsed = returnDate?.let {
            try {
                LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                Toast.makeText(this, "Ngày trả không hợp lệ.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Kiểm tra ngày trả sách không được nhỏ hơn ngày mượn
        if (returnDateParsed != null && returnDateParsed.isBefore(borrowDateParsed)) {
            Toast.makeText(
                this,
                "Ngày trả sách không thể trước ngày mượn sách.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val oldBorrowSlip = dbHelper.getBorrowSlipById(id)
        if (oldBorrowSlip != null) {
            // Nếu sách cũ khác sách mới, cập nhật số lượng tồn
            if (oldBorrowSlip.bookId != bookId) {
                dbHelper.updateBookQuantity(
                    oldBorrowSlip.bookId,
                    1
                ) // Tăng số lượng tồn sách cũ lên 1
                dbHelper.updateBookQuantity(bookId, -1) // Giảm số lượng tồn sách mới đi 1
            }

            // Nếu trạng thái thay đổi từ "Chưa trả sách" thành "Đã trả sách", tăng số lượng tồn sách
            if (oldBorrowSlip.status == "Chưa trả sách" && status == "Đã trả sách") {
                dbHelper.updateBookQuantity(bookId, 1)
            }

            // Nếu trạng thái thay đổi từ "Đã trả sách" thành "Chưa trả sách", giảm số lượng tồn sách
            if (oldBorrowSlip.status == "Đã trả sách" && status == "Chưa trả sách") {
                dbHelper.updateBookQuantity(bookId, -1)
            }
        }

        val borrowSlip = BorrowSlip(
            id = id,
            memberId = memberId,
            bookId = bookId,
            borrowDate = borrowDate,
            returnDate = returnDate
                ?: "", // Đảm bảo rằng returnDate không phải là null khi lưu vào database
            status = status
        )
        dbHelper.updateBorrowSlip(borrowSlip)
        loadBorrowSlips()
        clearFields()
        Toast.makeText(this, "Sửa phiếu mượn thành công.", Toast.LENGTH_SHORT).show()
        hideLayoutAdd()
    }


    private fun deleteBorrowSlip() {
        val id = etBorrowSlipId.text.toString().toIntOrNull()
        if (id != null) {
            // Tạo và hiển thị dialog xác nhận
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận xóa")
            builder.setMessage("Bạn có chắc chắn muốn xóa phiếu mượn này không?")
            builder.setPositiveButton("Có") { dialog, which ->
                // Xóa phiếu mượn nếu người dùng chọn "Có"
                dbHelper.deleteBorrowSlip(id)
                loadBorrowSlips()
                clearFields()
                Toast.makeText(this, "Xóa phiếu mượn thành công.", Toast.LENGTH_SHORT).show()
                hideLayoutAdd()
            }
            builder.setNegativeButton("Không") { dialog, which ->
                // Đóng dialog nếu người dùng chọn "Không"
                dialog.dismiss()
            }
            builder.show()
        } else {
            Toast.makeText(this, "Mã phiếu mượn không hợp lệ.", Toast.LENGTH_SHORT).show()
        }
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

    private fun showMemberSelectionDialog() {
        val members = dbHelper.getAllMembers()
        val memberNames = members.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Chọn thành viên")
            .setItems(memberNames) { _, which ->
                val selectedMember = members[which]
                etMemberId.setText(selectedMember.id.toString())
                etMemberName.setText(selectedMember.name)
            }
            .show()
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

    private fun clearFields() {
        etBorrowSlipId.text.clear()
        etMemberId.text.clear()
        etMemberName.text.clear()
        etBookId.text.clear()
        etBookName.text.clear()
        etBorrowDate.text.clear()
        etReturnDate.text.clear()
        rbTinhtrang.clearCheck()
        currentBorrowSlip = null
    }

    private fun showLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.VISIBLE
    }

    private fun hideLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.GONE
    }
}
