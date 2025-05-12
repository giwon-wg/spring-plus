package com.example.domain.todo.dto.response

import com.example.domain.user.dto.response.UserResponse
import java.time.LocalDateTime

data class TodoResponse(

    val id: Long,

    val title: String,

    val contents: String,

    val weather: String,

    val user: UserResponse,

    val createdAt: LocalDateTime,

    val modifiedAt: LocalDateTime
)
