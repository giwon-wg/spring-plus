package com.example.domain.comment.dto.response

import com.example.domain.user.dto.response.UserResponse
import lombok.Getter

data class CommentResponse(

    val id: Long,

    val contents: String,

    val user: UserResponse
)
