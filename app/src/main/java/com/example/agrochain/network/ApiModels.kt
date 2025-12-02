package com.example.agrochain.network

import com.example.agrochain.model.User
import com.example.agrochain.model.UserRole

data class LoginRequest(
    val email: String,
    val password: String,
    val role: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

data class AuthResponse(
    val message: String,
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val verified: Boolean
) {
    fun toUser(): User {
        return User(
            id = id,
            name = name,
            email = email,
            role = UserRole.valueOf(role),
            verified = verified
        )
    }
}

data class ErrorResponse(
    val error: String?
)


