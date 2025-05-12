package com.example.domain.user.enums

import com.example.domain.common.exception.InvalidRequestException

enum class UserRole {
    ADMIN, USER;

    companion object {
        @JvmStatic
        fun of(role: String?): UserRole {
            return entries.firstOrNull {it.name.equals(role, ignoreCase = true) }
                ?:throw InvalidRequestException("유효하지 않은 UerRole")
        }
    }
}
