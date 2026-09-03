package com.example.lingolens.domain.repository

import com.example.lingolens.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): AuthUser?
    fun observeAuthState(): Flow<AuthUser?>
    suspend fun loginWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun registerWithEmail(username: String, email: String, password: String): Result<AuthUser>
    suspend fun loginWithGoogle(idToken: String): Result<AuthUser>
    suspend fun logout()
}
