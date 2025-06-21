// com/insfinal/bookdforall/model/BookmarkInput.kt
package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class BookmarkInput(
    @Json(name = "user_id") val userId: Int,
    @Json(name = "book_id") val bookId: Int,
    val halaman: Int,
    val tanggal: String // YYYY-MM-DD
)