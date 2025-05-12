package com.example.domain.auth.service

import com.example.domain.common.exception.InvalidRequestException
import com.example.domain.user.entity.User
import com.example.domain.user.enums.UserRole.Companion.of
import com.example.domain.user.repository.UserRepository
import lombok.RequiredArgsConstructor
import com.example.config.JwtUtil
import com.example.domain.auth.dto.request.SigninRequest
import com.example.domain.auth.dto.request.SignupRequest
import com.example.domain.auth.dto.response.SigninResponse
import com.example.domain.auth.dto.response.SignupResponse
import com.example.domain.auth.exception.AuthException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AuthService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
){

    @Transactional
    fun signup(signupRequest: SignupRequest): SignupResponse {
        if (userRepository.existsByEmail(signupRequest.email)) {
            throw InvalidRequestException("이미 존재하는 이메일입니다.")
        }

        val encodedPassword = passwordEncoder.encode(signupRequest.password)

        val userRole = of(signupRequest.userRole)

        val newUser = User(
            email = signupRequest.email,
            password = encodedPassword,
            userRole = userRole,
            nickname = signupRequest.nickname
        )
        val savedUser = userRepository.save(newUser)

        val bearerToken = jwtUtil.createToken(
            userId = savedUser.id ?: throw IllegalAccessException("User Id가 null입니다."),
            email = savedUser.email,
            userRole = userRole,
            nickname = savedUser.nickname)

        return SignupResponse(bearerToken)
    }

    fun signin(signinRequest: SigninRequest): SigninResponse {
        val user = userRepository.findByEmail(signinRequest.email)
            .orElseThrow { InvalidRequestException("가입되지 않은 유저입니다.") }

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.password, user.password)) {
            throw AuthException("잘못된 비밀번호입니다.")
        }

        val bearerToken = jwtUtil.createToken(
            userId = user.id ?: throw IllegalAccessException("User Id가 null입니다."),
            email = user.email,
            userRole = user.userRole,
            nickname = user.nickname)

        return SigninResponse(bearerToken)
    }
}
