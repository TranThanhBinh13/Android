package com.example.app_tv.data.models

data class BookOrder(
    val id: Int,
    val bookId: Int,
    val supplierId: Int,
    val quantityOrder: Int, // Số lượng nhập
    val orderDate: String,
    var isStocked: Boolean // Đã nhập vào kho chưa
) {
    init {
        require(quantityOrder > 0) { "Số lượng nhập phải lớn hơn 0" }
    }
}
