package com.example.domain.common.dto

import com.example.domain.user.enums.UserRole


data class AuthUser(
    val id: Long,
    val email: String,
    val userRole: UserRole,
    val nickname: String
)
