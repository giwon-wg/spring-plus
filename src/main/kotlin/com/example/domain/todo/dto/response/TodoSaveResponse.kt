package com.example.domain.todo.dto.response

import com.example.domain.user.dto.response.UserResponse

class TodoSaveResponse(

    val id: Long,

    val title: String,

    val contents: String,

    val weather: String,

    val user: UserResponse
)
