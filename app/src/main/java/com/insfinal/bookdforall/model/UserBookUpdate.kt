// com/insfinal/bookdforall/model/UserBookUpdate.kt
package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class UserBookUpdate(
    @Json(name = "status_baca") val statusBaca: String? = null,
    @Json(name = "tanggal_baca") val tanggalBaca: String? = null,
    @Json(name = "page_terakhir") val pageTerakhir: Int? = null
)