package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.*
import com.insfinal.bookdforall.network.RetrofitInstance
import retrofit2.Response

class UserRepository {
    private val api = RetrofitInstance.api

    suspend fun getAll() = api.getUsers()
    suspend fun getOne(id: Int) = api.getUser(id)
    suspend fun create(req: CreateUserRequest) = api.createUser(req)
    suspend fun update(id: Int, req: CreateUserRequest) = api.updateUser(id, req)
    suspend fun delete(id: Int) = api.deleteUser(id)

    suspend fun login(req: LoginRequest): Response<LoginResponse> = api.login(req)
}
