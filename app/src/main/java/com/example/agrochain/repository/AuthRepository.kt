package com.example.agrochain.repository

import android.content.Context
import com.example.agrochain.model.User
import com.example.agrochain.model.UserRole
import com.example.agrochain.network.ApiClient
import com.example.agrochain.network.ErrorResponse
import com.example.agrochain.network.LoginRequest
import com.example.agrochain.network.RegisterRequest
import com.example.agrochain.network.TokenStorage
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class AuthRepository(private val context: Context) {
    private val apiService = ApiClient.apiService
    private val tokenStorage = TokenStorage(context)
    private val gson = Gson()

    suspend fun login(email: String, password: String, role: UserRole): Result<Pair<User, String>> {
        return try {
            val request = LoginRequest(
                email = email.trim(),
                password = password,
                role = role.name
            )
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val user = authResponse.user.toUser()
                tokenStorage.saveToken(authResponse.token)
                Result.success(Pair(user, authResponse.token))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.error ?: "Login failed"
                } catch (e: Exception) {
                    errorBody ?: "Login failed. Please check your credentials."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<Pair<User, String>> {
        return try {
            val request = RegisterRequest(
                name = name.trim(),
                email = email.trim(),
                password = password,
                role = role.name
            )
            val response = apiService.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val user = authResponse.user.toUser()
                tokenStorage.saveToken(authResponse.token)
                Result.success(Pair(user, authResponse.token))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.error ?: "Registration failed"
                } catch (e: Exception) {
                    errorBody ?: "Registration failed. Please try again."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenStorage.clearToken()
    }

    suspend fun getStoredToken(): String? {
        return tokenStorage.getToken().first()
    }
}
