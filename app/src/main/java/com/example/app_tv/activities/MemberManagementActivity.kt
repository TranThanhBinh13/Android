package com.example.app_tv.activities

import android.content.Intent
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
import com.example.app_tv.adapters.MemberAdapter
import com.example.app_tv.data.DBHelper
import com.example.app_tv.data.models.Member

class MemberManagementActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var imageView: ImageView
    private lateinit var buttonAdd: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonBack: Button
    private lateinit var buttonABC: Button
    private lateinit var txtSearch: EditText
    private lateinit var btnSearch: Button


    private lateinit var dbHelper: DBHelper
    private var currentMember: Member? = null
    private var imageUri: Uri? = null
    private var currentImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_management)

        dbHelper = DBHelper(this)

        // Initialize views
        listView = findViewById(R.id.member_list_view)
        editName = findViewById(R.id.edit_name)
        editEmail = findViewById(R.id.edit_email)
        imageView = findViewById(R.id.image_view)
        buttonAdd = findViewById(R.id.button_add)
        buttonEdit = findViewById(R.id.button_edit)
        buttonDelete = findViewById(R.id.button_delete)
        buttonBack = findViewById(R.id.button_back)
        buttonABC = findViewById(R.id.btnABC)
        txtSearch = findViewById(R.id.txtFind)
        btnSearch = findViewById(R.id.btnFind)

        // Load members into ListView
        loadMembers()

        // Set ListView item click listener
        listView.setOnItemClickListener { _, _, position, _ ->
            val member = listView.adapter.getItem(position) as Member
            currentMember = member
            populateMemberDetails(member)
            showLayoutAdd()
        }

        // Set button click listeners
        buttonAdd.setOnClickListener { addMember() }
        buttonEdit.setOnClickListener { editMember() }
        buttonDelete.setOnClickListener { deleteMember() }
        buttonBack.setOnClickListener { showBackConfirmationDialog() }
        imageView.setOnClickListener { openImageChooser() }
        buttonABC.setOnClickListener {
            clearFields()
            hideLayoutAdd()
        }
        btnSearch.setOnClickListener {
            searchMember()
            showLayoutAdd()
        }

        // Add text change listener to txtSearch
        txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchMember()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchMember() {
        val query = txtSearch.text.toString()
        if (query.isNotBlank()) {
            val members = dbHelper.searchMembers(query)
            val adapter = MemberAdapter(this, members)
            listView.adapter = adapter
        } else {
            loadMembers()
        }
    }

    private fun loadMembers() {
        val members = dbHelper.getAllMembers()
        val adapter = MemberAdapter(this, members)
        listView.adapter = adapter
    }

    private fun populateMemberDetails(member: Member) {
        editName.setText(member.name)
        editEmail.setText(member.email)
        if (member.imageUri != null) {
            imageView.setImageURI(Uri.parse(member.imageUri))
            currentImageUri = member.imageUri // Cập nhật currentImageUri
        } else {
            imageView.setImageResource(R.drawable.ic_avt) // Placeholder image
            currentImageUri = null
        }
    }

    private fun addMember() {
        val name = editName.text.toString()
        val email = editEmail.text.toString()
        if (name.isNotBlank() && email.isNotBlank()) {
            val newMember = Member(0, name, email, imageUri?.toString())
            dbHelper.addMember(newMember)
            loadMembers()
            clearFields()
            showToast("Thêm thành viên thành công")
            hideLayoutAdd()
        } else {
            showToast("Vui lòng điền đầy đủ thông tin")
        }
    }

    private fun editMember() {
        val name = editName.text.toString()
        val email = editEmail.text.toString()
        currentMember?.let {
            if (name.isNotBlank() && email.isNotBlank()) {
                val updatedMember =
                    Member(it.id, name, email, currentImageUri) // Sử dụng currentImageUri
                dbHelper.updateMember(updatedMember)
                loadMembers()
                clearFields()
                showToast("Cập nhật thành viên thành công")
                hideLayoutAdd()
            } else {
                showToast("Vui lòng điền đầy đủ thông tin")
            }
        } ?: showToast("Chưa chọn thành viên để chỉnh sửa")
    }

    private fun deleteMember() {
        currentMember?.let {
            // Tạo và hiển thị dialog xác nhận
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận xóa")
            builder.setMessage("Bạn có chắc chắn muốn xóa thành viên này không?")
            builder.setPositiveButton("Có") { dialog, which ->
                // Xóa thành viên nếu người dùng chọn "Có"
                dbHelper.deleteMember(it.id)
                loadMembers()
                clearFields()
                showToast("Xóa thành viên thành công")
                hideLayoutAdd()
            }
            builder.setNegativeButton("Không") { dialog, which ->
                // Đóng dialog nếu người dùng chọn "Không"
                dialog.dismiss()
            }
            builder.show()
        } ?: showToast("Chưa chọn thành viên để xóa")
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
        val imageNames = resources.getStringArray(R.array.image_names_avt)

        // Chuyển đổi tên ảnh thành các resource IDs
        val images = imageNames.map { imageName ->
            resources.getIdentifier(imageName, "drawable", packageName)
        }.toTypedArray()

        // Hiển thị dialog để người dùng chọn ảnh
        AlertDialog.Builder(this)
            .setTitle("Chọn ảnh")
            .setItems(images.map { resources.getResourceEntryName(it) }
                .toTypedArray()) { _, which ->
                imageUri = Uri.parse("android.resource://${packageName}/${images[which]}")
                imageView.setImageURI(imageUri)
                currentImageUri = imageUri?.toString() // Cập nhật currentImageUri
            }
            .show()
    }

    private fun clearFields() {
        editName.text.clear()
        editEmail.text.clear()
        imageView.setImageResource(R.drawable.ic_avt)
        currentMember = null
        currentImageUri = null // Xóa giá trị currentImageUri đã lưu
    }

    private fun showLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.VISIBLE
    }

    private fun hideLayoutAdd() {
        findViewById<LinearLayout>(R.id.layoutadd).visibility = View.GONE
    }
}
