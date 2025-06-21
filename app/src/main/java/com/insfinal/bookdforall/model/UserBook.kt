// com/insfinal/bookdforall/model/UserBook.kt
package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class UserBook(
    // Asumsi ada ID unik untuk setiap entri user-book, jika tidak ada, bisa dihilangkan
    // @Json(name = "user_book_id") val userBookId: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "book_id") val bookId: Int,
    @Json(name = "status_baca") val statusBaca: String,
    @Json(name = "tanggal_baca") val tanggalBaca: String,
    @Json(name = "page_terakhir") val pageTerakhir: Int
)