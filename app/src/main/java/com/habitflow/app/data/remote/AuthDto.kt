package com.habitflow.app.data.remote

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val identityStatement: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val userId: String,
    val token: String,
    val refreshToken: String,
    val displayName: String? = null
)

data class RefreshRequest(
    val refreshToken: String
)
