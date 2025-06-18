package com.insfinal.bookdforall.network

import com.example.booksforall.model.*
import retrofit2.Response
import retrofit2.http.*
import com.example.booksforall.model.Book
import com.example.booksforall.model.CreateBookRequest

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

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    // Admins
    @GET("admins") suspend fun getAdmins(): Response<List<Admin>>
    @GET("admins/{id}") suspend fun getAdmin(@Path("id") id: Int): Response<Admin>
    @POST("admins") suspend fun createAdmin(@Body req: CreateAdminRequest): Response<Unit>
    @PUT("admins/{id}") suspend fun updateAdmin(@Path("id") id: Int, @Body req: CreateAdminRequest): Response<Unit>
    @DELETE("admins/{id}") suspend fun deleteAdmin(@Path("id") id: Int): Response<Unit>

    @GET("books")
    suspend fun getBooks(): Response<List<Book>>

    @GET("books/{id}")
    suspend fun getBook(@Path("id") id: Int): Response<Book>

    @POST("books")
    suspend fun createBook(@Body req: CreateBookRequest): Response<Unit>

    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: Int, @Body req: CreateBookRequest): Response<Unit>

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Response<Unit>

    // --- Publishers ---
    // GET /publishers: Get all publishers
    @GET("publishers")
    suspend fun getPublishers(): Response<List<Publisher>>

    // POST /publishers: Create a new publisher
    @POST("publishers")
    suspend fun createPublisher(@Body req: PublisherInput): Response<Unit>

    // GET /publishers/{id}: Get publisher by ID
    @GET("publishers/{id}")
    suspend fun getPublisher(@Path("id") id: Int): Response<Publisher>

    // PUT /publishers/{id}: Update publisher by ID
    @PUT("publishers/{id}")
    suspend fun updatePublisher(@Path("id") id: Int, @Body req: PublisherInput): Response<Unit>

    // DELETE /publishers/{id}: Delete publisher by ID
    @DELETE("publishers/{id}")
    suspend fun deletePublisher(@Path("id") id: Int): Response<Unit>


    // --- PublisherRegistrations ---
    // GET /publisher-registrations: Get all registrations
    @GET("publisher-registrations")
    suspend fun getPublisherRegistrations(): Response<List<PublisherRegistrationInput>> // Asumsi respons adalah list of input

    // POST /publisher-registrations: Create a new registration
    @POST("publisher-registrations")
    suspend fun createPublisherRegistration(@Body req: PublisherRegistrationInput): Response<Unit>

    // GET /publisher-registrations/{id}: Get registration by ID
    @GET("publisher-registrations/{id}")
    suspend fun getPublisherRegistration(@Path("id") id: Int): Response<PublisherRegistrationInput> // Asumsi respons adalah input tunggal

    // PUT /publisher-registrations/{id}: Update registration by ID
    @PUT("publisher-registrations/{id}")
    suspend fun updatePublisherRegistration(@Path("id") id: Int, @Body req: PublisherRegistrationUpdate): Response<Unit>

    // DELETE /publisher-registrations/{id}: Delete registration by ID
    @DELETE("publisher-registrations/{id}")
    suspend fun deletePublisherRegistration(@Path("id") id: Int): Response<Unit>
}