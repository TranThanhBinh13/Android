package com.example.app_tv.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.data.DBHelper
import com.example.app_tv.data.models.BookBorrowStat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {

    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var btnGenerateReport: Button
    private lateinit var btnChart: Button
    private lateinit var listViewBookReport: ListView
    private lateinit var bottomNavigationView: BottomNavigationView

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    private lateinit var bookBorrowStats: List<BookBorrowStat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        btnGenerateReport = findViewById(R.id.btnGenerateReport)
        btnChart = findViewById(R.id.btnChart)
        listViewBookReport = findViewById(R.id.listViewBookReport)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        etStartDate.setOnClickListener {
            showDatePicker { date ->
                etStartDate.setText(date)
                updateEndDate(date)
            }
        }

        btnGenerateReport.setOnClickListener {
            val startDate = etStartDate.text.toString()
            val endDate = etEndDate.text.toString()
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val dbHelper = DBHelper(this)
                bookBorrowStats = dbHelper.getBooksBorrowedWithinDateRange(startDate, endDate)
                val adapter =
                    BookBorrowStatAdapter(this, R.layout.list_borrowbook_item, bookBorrowStats)
                listViewBookReport.adapter = adapter
            } else {
                Toast.makeText(
                    this,
                    "Vui lòng chọn ngày bắt đầu và ngày kết thúc",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        btnChart.setOnClickListener {
            val startDate = etStartDate.text.toString()
            val endDate = etEndDate.text.toString()

            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val dbHelper = DBHelper(this)
                val bookBorrowStats = dbHelper.getBooksBorrowedWithinDateRange(startDate, endDate)

                val intent = Intent(this, ReportChartActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList("bookBorrowStats", ArrayList(bookBorrowStats))
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Vui lòng chọn ngày bắt đầu và ngày kết thúc",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_report -> true
                R.id.nav_news -> {
                    val intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_profile -> {
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentLocale = resources.configuration.locale
        val locale = Locale("vi", "VN")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val formattedDate = dateFormat.format(date.time)
                onDateSet(formattedDate)
                Locale.setDefault(currentLocale)
                val config = Configuration()
                config.locale = currentLocale
                resources.updateConfiguration(config, resources.displayMetrics)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.setOnDismissListener {
            Locale.setDefault(currentLocale)
            val config = Configuration()
            config.locale = currentLocale
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        datePicker.show()
    }

    private fun updateEndDate(startDate: String) {
        val endDate = getEndDate(startDate)
        etEndDate.setText(endDate)
    }

    private fun getEndDate(startDate: String): String {
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(startDate) ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        return dateFormat.format(calendar.time)
    }
}
