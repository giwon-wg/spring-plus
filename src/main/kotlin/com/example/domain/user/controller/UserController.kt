package com.example.domain.user.controller

import lombok.RequiredArgsConstructor
import com.example.domain.common.dto.AuthUser
import com.example.domain.user.dto.request.UserChangePasswordRequest
import com.example.domain.user.dto.response.UserResponse
import com.example.domain.user.entity.User
import com.example.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequiredArgsConstructor
class UserController(
    private val userService: UserService
) {

    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.getUser(userId))
    }

    @PutMapping("/users")
    fun changePassword(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestBody userChangePasswordRequest: UserChangePasswordRequest
    ) {
        userService.changePassword(authUser.id, userChangePasswordRequest)
    }

    @GetMapping("/user/nickname")
    fun getUserByNickname(@RequestParam nickname: String): ResponseEntity<User> {
        val user = userService.getUserByNickname(nickname)
        return ResponseEntity.ok(user)
    }
}
