package com.insfinal.bookdforall.model

data class LoginResponse(
    val message: String,
    val user: UserSummary
)

data class UserSummary(
    val id: Int,
    val nama: String,
    val email: String,
    val isAdmin: Boolean
)
