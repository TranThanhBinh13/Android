package com.example.app_tv.data.models

data class BorrowSlip(
    val id: Int,
    val memberId: Int,
    val bookId: Int,
    val borrowDate: String, // Ngày mượn sách
    val returnDate: String?, // Ngày trả sách (có thể là null nếu chưa trả)
    val status: String // Trạng thái (Có thể là "Đã trả sách" hoặc "Chưa trả sách")
) {
    companion object {
        const val STATUS_RETURNED = "Đã trả sách"
        const val STATUS_NOT_RETURNED = "Chưa trả sách"
    }

    fun isReturned(): Boolean {
        return status == STATUS_RETURNED
    }
}
