package com.example.domain.manager.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import com.example.domain.common.dto.AuthUser
import com.example.domain.manager.dto.request.ManagerSaveRequest
import com.example.domain.manager.dto.response.ManagerResponse
import com.example.domain.manager.dto.response.ManagerSaveResponse
import com.example.domain.manager.service.ManagerService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequiredArgsConstructor
class ManagerController(
    private val managerService: ManagerService
) {

    @PostMapping("/todos/{todoId}/managers")
    fun saveManager(
        @AuthenticationPrincipal authUser: AuthUser,
        @PathVariable todoId: Long,
        @RequestBody @Valid managerSaveRequest: ManagerSaveRequest
    ): ResponseEntity<ManagerSaveResponse> {
        return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerSaveRequest))
    }

    @GetMapping("/todos/{todoId}/managers")
    fun getMembers(@PathVariable todoId: Long): ResponseEntity<List<ManagerResponse>> {
        return ResponseEntity.ok(managerService.getManagers(todoId))
    }

    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    fun deleteManager(
        @AuthenticationPrincipal authUser: AuthUser,
        @PathVariable todoId: Long,
        @PathVariable managerId: Long
    ) {
        managerService.deleteManager(authUser, todoId, managerId)
    }
}
