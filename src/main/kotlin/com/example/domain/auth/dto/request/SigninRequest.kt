package com.example.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SigninRequest (

    @field:NotBlank
    @field:Email
    var email: String,

    @field:NotBlank
    var password: String
)
