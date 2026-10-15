package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AuthData
import com.example.data.model.LoginRequest
import com.example.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("youtube_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USERNAME = "auth_username"
        private const val KEY_USER_ID = "auth_user_id"
    }

    init {
        val savedToken = prefs.getString(KEY_TOKEN, null)
        if (!savedToken.isNullOrBlank()) {
            ApiClient.currentToken = savedToken
        }
    }

    fun isLoggedIn(): Boolean {
        return !prefs.getString(KEY_TOKEN, null).isNullOrBlank()
    }

    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: ""
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    suspend fun login(username: String, password: String): Result<AuthData> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.login(LoginRequest(username.trim(), password))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return@withContext Result.failure(Exception("Empty login response"))
                saveSession(data)
                Result.success(data)
            } else {
                val errorMsg = response.body()?.error?.message 
                    ?: response.errorBody()?.string() 
                    ?: "Login failed with HTTP ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(username: String, password: String): Result<AuthData> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.signup(LoginRequest(username.trim(), password))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return@withContext Result.failure(Exception("Empty signup response"))
                saveSession(data)
                Result.success(data)
            } else {
                val errorMsg = response.body()?.error?.message 
                    ?: response.errorBody()?.string() 
                    ?: "Signup failed with HTTP ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        ApiClient.currentToken = null
    }

    private fun saveSession(data: AuthData) {
        val token = data.apiToken?.token
        prefs.edit().apply {
            putString(KEY_USERNAME, data.user.username)
            putString(KEY_USER_ID, data.user.id)
            if (!token.isNullOrBlank()) {
                putString(KEY_TOKEN, token)
                ApiClient.currentToken = token
            }
        }.apply()
    }
}
