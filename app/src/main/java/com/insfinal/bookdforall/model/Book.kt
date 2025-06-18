package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

/**
 * Representasi entitas Buku sesuai ERD.
 */
data class Book(
    @Json(name = "book_id") val bookId: Int,
    val judul: String,
    val penulis: String,
    val deskripsi: String,
    val kategori: String,
    val harga: Double,
    val format: String,
    @Json(name = "cover_image_url") val coverImageUrl: String,
    @Json(name = "publisher_id") val publisherId: Int?,
    @Json(name = "content_provider_id") val contentProviderId: Int?
)
