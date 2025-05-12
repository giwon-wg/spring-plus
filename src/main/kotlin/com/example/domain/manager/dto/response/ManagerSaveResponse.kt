package com.example.domain.manager.dto.response

import com.example.domain.user.dto.response.UserResponse

data class ManagerSaveResponse(

    val id: Long,

    val user: UserResponse
)
