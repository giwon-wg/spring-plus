package com.example.domain.auth.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import com.example.domain.auth.dto.request.SigninRequest
import com.example.domain.auth.dto.request.SignupRequest
import com.example.domain.auth.dto.response.SigninResponse
import com.example.domain.auth.dto.response.SignupResponse
import com.example.domain.auth.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/auth/signup")
    fun signup(@RequestBody signupRequest: @Valid SignupRequest): SignupResponse {
        return authService.signup(signupRequest)
    }

    @PostMapping("/auth/signin")
    fun signin(@RequestBody signinRequest: @Valid SigninRequest): SigninResponse {
        return authService.signin(signinRequest)
    }
}
