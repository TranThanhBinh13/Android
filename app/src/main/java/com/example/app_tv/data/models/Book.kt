package com.example.app_tv.data.models

data class Book(
    val id: Int,
    val name: String,
    val imageUri: String?,
    val categoryId: Int,
    val quantity: Int, // số lượng tồn trong thư viện
    val price: Double
)
