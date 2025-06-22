package com.insfinal.bookdforall.network

import com.insfinal.bookdforall.model.*
import retrofit2.Response
import retrofit2.http.*
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.model.ChangePasswordRequest

interface ApiService {
    // existing endpoints...
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
    // Base URL akan ditentukan di RetrofitInstance
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @POST("users")
    suspend fun createUser(@Body body: CreateUserRequest): Response<Unit>

    // jika ingin endpoint buang akun, update dsb:
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body body: CreateUserRequest): Response<Unit>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @PUT("users/change-password")
    suspend fun changePassword(@Body req: ChangePasswordRequest): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest): Response<Unit>

    @POST("auth/reset-password")
    suspend fun setNewPassword(@Body req: SetNewPasswordRequest): Response<Unit>

    // Admins
    @GET("admins") suspend fun getAdmins(): Response<List<Admin>>
    @GET("admins/{id}") suspend fun getAdmin(@Path("id") id: Int): Response<Admin>
    @POST("admins") suspend fun createAdmin(@Body req: CreateAdminRequest): Response<Unit>
    @PUT("admins/{id}") suspend fun updateAdmin(@Path("id") id: Int, @Body req: CreateAdminRequest): Response<Unit>
    @DELETE("admins/{id}") suspend fun deleteAdmin(@Path("id") id: Int): Response<Unit>

    @GET("books")
    suspend fun getBooks(): Response<List<Book>>

    @GET("books/{id}")
    suspend fun getBookById(@Path("id") id: Int): Response<Book>

    @POST("books")
    suspend fun createBook(@Body req: CreateBookRequest): Response<Unit>

    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: Int, @Body req: CreateBookRequest): Response<Unit>

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Response<Unit>

    @GET("books/continue")
    suspend fun getContinueReading(): Response<List<Book>>

    @GET("books/trending")
    suspend fun getTrendingBooks(): Response<List<Book>>
}

