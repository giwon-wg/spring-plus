package com.example.domain.user.dto.request

import jakarta.validation.constraints.NotBlank

data class UserChangePasswordRequest (

    @field:NotBlank
    var oldPassword: String,

    @field:NotBlank
    var newPassword: String
)

