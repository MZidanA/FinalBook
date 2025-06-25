package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.*
import com.insfinal.bookdforall.network.RetrofitInstance
import retrofit2.Response

class UserRepository {
    private val api = RetrofitInstance.api

    suspend fun getOne(id: Int) = api.getUser(id)
    suspend fun create(req: CreateUserRequest) = api.createUser(req)
    suspend fun updateUser(id: Int, req: CreateUserRequest) = api.updateUser(id, req)
    suspend fun getCurrentUser(): Response<User> = api.getCurrentUser()
    suspend fun changePassword(oldPass: String, newPass: String): Response<Unit> {
        return api.changePassword(ChangePasswordRequest(oldPass, newPass))
    }
    suspend fun login(req: LoginRequest): Response<LoginResponse> = api.login(req)
    suspend fun forgotPassword(email: String): Response<Unit> {
        return RetrofitInstance.api.forgotPassword(ForgotPasswordRequest(email))
    }
    suspend fun getAllUsers(): Response<List<User>> {
        return api.getUsers()
    }
    suspend fun deleteUser(userId: Int): Response<Unit> {
        return api.deleteUser(userId)
    }

}
