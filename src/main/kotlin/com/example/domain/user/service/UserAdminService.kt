package com.example.domain.user.service

import lombok.RequiredArgsConstructor
import com.example.domain.common.exception.InvalidRequestException
import com.example.domain.user.dto.request.UserRoleChangeRequest
import com.example.domain.user.enums.UserRole
import com.example.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
class UserAdminService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun changeUserRole(userId: Long, userRoleChangeRequest: UserRoleChangeRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { InvalidRequestException("User not found") }

        user.updateRole(UserRole.of(userRoleChangeRequest.role))
    }
}
