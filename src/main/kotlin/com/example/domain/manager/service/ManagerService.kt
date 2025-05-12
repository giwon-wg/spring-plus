package com.example.domain.manager.service

import com.example.domain.todo.repository.TodoRepository
import com.example.domain.user.dto.response.UserResponse
import com.example.domain.user.entity.User
import com.example.domain.user.repository.UserRepository
import lombok.RequiredArgsConstructor
import com.example.domain.common.dto.AuthUser
import com.example.domain.common.exception.InvalidRequestException
import com.example.domain.manager.dto.request.ManagerSaveRequest
import com.example.domain.manager.dto.response.ManagerResponse
import com.example.domain.manager.dto.response.ManagerSaveResponse
import com.example.domain.manager.entity.Manager
import com.example.domain.manager.repository.ManagerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ManagerService(

    private val managerRepository: ManagerRepository,

    private val userRepository: UserRepository,

    private val todoRepository: TodoRepository,

    private val logService: LogService
) {

    @Transactional
    fun saveManager(authUser: AuthUser, todoId: Long, managerSaveRequest: ManagerSaveRequest): ManagerSaveResponse {
        try {
            // 일정을 만든 유저
            val user = User.fromAuthUser(authUser)
            val todo = todoRepository.findById(todoId)
                .orElseThrow { InvalidRequestException("Todo not found") }

            if (todo.user.id != user.id) {
                throw InvalidRequestException("담당자를 등록하려고 하는 유저가 유효하지 않거나, 일정을 만든 유저가 아닙니다.")
            }

            val managerUser = userRepository.findById(managerSaveRequest.managerUserId)
                .orElseThrow { InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다.") }

            if (managerUser.id == user.id) {
                throw InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.")
            }

            val savedManagerUser = managerRepository.save(Manager(managerUser, todo))

            return ManagerSaveResponse(
                id = savedManagerUser.id!!,
                user = UserResponse(managerUser.id!!, managerUser.email)
            )
        } finally {
            try {
                logService.saveLog(
                    action = "매니저 요청",
                    details = "userId: ${authUser.id}, todoId: $todoId, managerUserId: ${managerSaveRequest.managerUserId}"
                )
            } catch (e: Exception) {
                println("로그 저장 실패: ${e.message}")
            }
        }
    }

    fun getManagers(todoId: Long): List<ManagerResponse> {
        val todo = todoRepository.findById(todoId)
            .orElseThrow { InvalidRequestException("Todo not found") }

        val managerList = managerRepository.findByTodoIdWithUser(todo.id!!)

        return managerList.map { manager ->
            val user = manager.user
            ManagerResponse(
                id = manager.id!!,
                user = UserResponse(user.id!!, user.email)
            )
        }
    }

    @Transactional
    fun deleteManager(authUser: AuthUser, todoId: Long, managerId: Long) {
        val user = User.fromAuthUser(authUser)

        val todo = todoRepository.findById(todoId)
            .orElseThrow { InvalidRequestException("Todo not found") }

        if (todo.user.id != user.id) {
            throw InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.")
        }

        val manager = managerRepository.findById(managerId)
            .orElseThrow { InvalidRequestException("Manager not found") }

        if (manager.todo.id != todo.id) {
            throw InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.")
        }

        managerRepository.delete(manager)
    }
}
