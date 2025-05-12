package com.example.domain.user.dto.request

import jakarta.validation.constraints.NotBlank

data class UserRoleChangeRequest(

    @field:NotBlank
    var role: String
)
