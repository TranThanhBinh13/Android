package com.example.app_tv.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R
import com.example.app_tv.data.models.BookBorrowStat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ReportChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var bookBorrowStats: ArrayList<BookBorrowStat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        barChart = findViewById(R.id.barChart)

        // Nhận dữ liệu từ Intent
        val bundle = intent.extras
        bookBorrowStats = bundle?.getParcelableArrayList("bookBorrowStats") ?: arrayListOf()

        // Cấu hình biểu đồ để ẩn "description label"
        barChart.description.isEnabled = false

        // Prepare data for BarChart
        val entries = bookBorrowStats.mapIndexed { index, stat ->
            BarEntry(index.toFloat(), stat.borrowCount.toFloat())
        }
        val dataSet = BarDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData = BarData(dataSet)
        barChart.data = barData

        // Set X-axis labels to book names
        val xAxis = barChart.xAxis
        val xAxisLabels = bookBorrowStats.map { it.bookName }
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.labelRotationAngle = -45f // Xoay tên sách một góc 45 độ

        barChart.invalidate() // Refresh the chart
    }
}
