package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.*
import com.insfinal.bookdforall.network.RetrofitInstance
import retrofit2.Response // Import Response

class AdminRepository {
    private val api = RetrofitInstance.api

    suspend fun getAll(): Response<List<Admin>> = api.getAdmins() // Tambahkan Response
    suspend fun getOne(id: Int): Response<Admin> = api.getAdmin(id) // Tambahkan Response
    suspend fun create(req: CreateAdminRequest): Response<Unit> = api.createAdmin(req) // Tambahkan Response<Unit>
    suspend fun update(id: Int, req: CreateAdminRequest): Response<Unit> = api.updateAdmin(id, req) // Tambahkan Response<Unit>
    suspend fun delete(id: Int): Response<Unit> = api.deleteAdmin(id) // Tambahkan Response<Unit>
}