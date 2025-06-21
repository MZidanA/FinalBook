package com.insfinal.bookdforall.network

import com.insfinal.bookdforall.model.* // Import semua model yang dibutuhkan
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- Authentication ---
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    // --- Users ---
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @POST("users")
    suspend fun createUser(@Body body: CreateUserRequest): Response<Unit>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body body: CreateUserRequest): Response<Unit>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    // --- Admins (Dipastikan ada dan sesuai dengan AdminRepository) ---
    @GET("admins") suspend fun getAdmins(): Response<List<Admin>>
    @GET("admins/{id}") suspend fun getAdmin(@Path("id") id: Int): Response<Admin>
    @POST("admins") suspend fun createAdmin(@Body req: CreateAdminRequest): Response<Unit>
    @PUT("admins/{id}") suspend fun updateAdmin(@Path("id") id: Int, @Body req: CreateAdminRequest): Response<Unit>
    @DELETE("admins/{id}") suspend fun deleteAdmin(@Path("id") id: Int): Response<Unit>

    // --- Books ---
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

    // --- Reviews ---
    @POST("reviews")
    suspend fun createReview(@Body review: ReviewInput): Response<Unit>

    // --- Bookmarks ---
    @GET("bookmarks")
    suspend fun getBookmarks(@Query("userId") userId: Int? = null): Response<List<BookmarkInput>>

    @POST("bookmarks")
    suspend fun createBookmark(@Body bookmark: BookmarkInput): Response<Unit>

    // --- UserBooks ---
    @GET("user-books/user/{userId}")
    suspend fun getUserBooksByUserId(@Path("userId") userId: Int): Response<List<UserBook>>

    @PUT("user-books/{userId}/{bookId}")
    suspend fun updateUserBook(
        @Path("userId") userId: Int,
        @Path("bookId") bookId: Int,
        @Body userBook: UserBookUpdate
    ): Response<Unit>

    // Tambahkan metode yang mungkin dibutuhkan oleh repository lain jika belum ada
    // Misal:
    // @GET("reviews/{id}") suspend fun getReview(@Path("id") id: Int): Response<ReviewInput>
    // @PUT("reviews/{id}") suspend fun updateReview(@Path("id") id: Int, @Body review: ReviewUpdate): Response<Unit>
    // @DELETE("reviews/{id}") suspend fun deleteReview(@Path("id") id: Int): Response<Unit>
    // @GET("bookmarks/{id}") suspend fun getBookmark(@Path("id") id: Int): Response<BookmarkInput>
    // @PUT("bookmarks/{id}") suspend fun updateBookmark(@Path("id") id: Int, @Body bookmark: BookmarkUpdate): Response<Unit>
    // @DELETE("bookmarks/{id}") suspend fun deleteBookmark(@Path("id") id: Int): Response<Unit>
    // @GET("user-books") suspend fun getUserBooks(): Response<List<UserBook>>
    // @POST("user-books") suspend fun createUserBook(@Body userBook: UserBookInput): Response<Unit>
    // @DELETE("user-books/{userId}/{bookId}") suspend fun deleteUserBook(@Path("userId") userId: Int, @Path("bookId") bookId: Int): Response<Unit>
}