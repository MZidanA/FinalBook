package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.network.RetrofitInstance
import retrofit2.Response

class BookRepository {
    private val api = RetrofitInstance.api

    suspend fun getAll(): Response<List<Book>> = api.getBooks()
    suspend fun getOne(id: Int): Response<Book> = api.getBook(id)
    suspend fun create(req: CreateBookRequest): Response<Unit> = api.createBook(req)
    suspend fun update(id: Int, req: CreateBookRequest): Response<Unit> = api.updateBook(id, req)
    suspend fun delete(id: Int): Response<Unit> = api.deleteBook(id)
}