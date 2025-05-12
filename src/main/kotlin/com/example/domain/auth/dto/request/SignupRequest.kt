package com.example.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor

data class SignupRequest(

    @field:NotBlank
    @field:Email
    var email: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    var userRole: String,

    @field:NotBlank
    var nickname: String
)
