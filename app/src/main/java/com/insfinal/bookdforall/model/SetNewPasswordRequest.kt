package com.insfinal.bookdforall.model

data class SetNewPasswordRequest(
    val newPassword: String,
    val email: String
)