package com.insfinal.bookdforall.repository

import android.content.Context
import android.net.Uri
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import com.insfinal.bookdforall.utils.FileUtils
import okhttp3.RequestBody.Companion.asRequestBody


class BookRepository {
    private val api = RetrofitInstance.api

    suspend fun getAll(): Response<List<Book>> = api.getBooks()

    suspend fun getBookById(id: Int): Response<Book> = api.getBookById(id)

    suspend fun create(req: CreateBookRequest): Response<Unit> = api.createBook(req)

    suspend fun update(id: Int, req: CreateBookRequest): Response<Unit> = api.updateBook(id, req)

    suspend fun uploadBook(
        context: Context,
        title: String,
        author: String,
        description: String,
        coverUri: Uri?,
        pdfUri: Uri?
    ){
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val authorPart = author.toRequestBody("text/plain".toMediaTypeOrNull())
        val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

        val coverPart = coverUri?.let {
            val file = FileUtils.fromUri(context, coverUri)
            file.asRequestBody("image/*".toMediaTypeOrNull())
                .let { body -> MultipartBody.Part.createFormData("cover", file.name, body) }
        }

        val pdfPart = pdfUri?.let {
            val file = FileUtils.fromUri(context, pdfUri)
            file.asRequestBody("application/pdf".toMediaTypeOrNull())
                .let { body -> MultipartBody.Part.createFormData("pdf", file.name, body) }
        }

        api.uploadBook(titlePart, authorPart, descPart, coverPart, pdfPart)
    }

    suspend fun delete(id: Int): Response<Unit> = api.deleteBook(id)

    suspend fun getContinueReadingBooks(): Response<List<Book>> = api.getContinueReading()

    suspend fun getTrendingBooks(): Response<List<Book>> = api.getTrendingBooks()
}