package com.insfinal.bookdforall.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize // Tambahkan import ini

@Parcelize
data class Book(
    @Json(name = "book_id") val bookId: Int,
    val judul: String = "",
    val penulis: String = "",
    val deskripsi: String = "",
    val kategori: String = "",
    val harga: Double,
    val format: String = "",
    val rating: Float?,
    val ratingCount: Int?,
    val releaseDate: String?,
    val totalPages: Int?,
    @Json(name = "cover_image_url") val coverImageUrl: String,
    @Json(name = "publisher_id") val publisherId: Int?,
    @Json(name = "content_provider_id") val contentProviderId: Int?
) : Parcelable // Implementasikan Parcelable