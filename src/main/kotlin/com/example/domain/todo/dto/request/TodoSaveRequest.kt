package com.example.domain.todo.dto.request

import jakarta.validation.constraints.NotBlank

data class TodoSaveRequest(

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val contents: String
)