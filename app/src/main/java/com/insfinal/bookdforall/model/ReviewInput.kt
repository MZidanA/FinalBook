// com/insfinal/bookdforall/model/ReviewInput.kt
package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class ReviewInput(
    @Json(name = "user_id") val userId: Int,
    @Json(name = "book_id") val bookId: Int,
    val rating: Int,
    val komentar: String,
    @Json(name = "tanggal_review") val tanggalReview: String // YYYY-MM-DD
)