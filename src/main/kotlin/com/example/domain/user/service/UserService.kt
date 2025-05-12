package com.example.domain.user.service

import com.example.domain.common.exception.InvalidRequestException
import com.example.domain.user.dto.request.UserChangePasswordRequest
import com.example.domain.user.dto.response.UserResponse
import com.example.domain.user.entity.User
import com.example.domain.user.repository.UserRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getUser(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("User not found") }

        return UserResponse(
            id = user.id!!,
            email = user.email
        )
    }

    @Transactional
    fun changePassword(userId: Long, userChangePasswordRequest: UserChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest)

        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("User not found") }

        if (passwordEncoder.matches(userChangePasswordRequest.newPassword, user.password)) {
            throw InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.")
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.oldPassword, user.password)) {
            throw InvalidRequestException("잘못된 비밀번호입니다.")
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.newPassword))
    }

    @Cacheable(value = ["userByNickname"], key = "#nickname", unless = "#result == null")
    fun getUserByNickname(nickname: String): User {
        return userRepository.findByNickname(nickname)
            .orElseThrow { IllegalArgumentException("사용자가 없습니다.") }
    }

    companion object {
        private fun validateNewPassword(userChangePasswordRequest: UserChangePasswordRequest) {
            if (userChangePasswordRequest.newPassword.length < 8 ||
                !userChangePasswordRequest.newPassword.matches(".*\\d.*".toRegex()) ||
                !userChangePasswordRequest.newPassword.matches(".*[A-Z].*".toRegex())
            ) {
                throw InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.")
            }
        }
    }
}
