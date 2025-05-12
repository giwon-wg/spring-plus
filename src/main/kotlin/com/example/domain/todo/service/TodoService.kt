package com.example.domain.todo.service

import com.example.domain.user.dto.response.UserResponse
import com.example.domain.user.entity.User
import com.example.client.WeatherClient
import com.example.domain.common.dto.AuthUser
import com.example.domain.common.exception.InvalidRequestException
import com.example.domain.todo.dto.request.TodoSaveRequest
import com.example.domain.todo.dto.response.TodoResponse
import com.example.domain.todo.dto.response.TodoSaveResponse
import com.example.domain.todo.dto.response.TodoSummaryResponse
import com.example.domain.todo.entity.Todo
import com.example.domain.todo.repository.TodoQueryRepository
import com.example.domain.todo.repository.TodoRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class TodoService (
    private val todoRepository: TodoRepository,
    private val weatherClient: WeatherClient,
    private val todoQueryRepository: TodoQueryRepository
) {
    @Transactional
    fun saveTodo(authUser: AuthUser, request: TodoSaveRequest): TodoSaveResponse {
        val user = User.fromAuthUser(authUser)
        val weather = weatherClient.todayWeather

        val saved = todoRepository.save(
            Todo(request.title, request.contents, weather, user)
        )

        return TodoSaveResponse(
            id = saved.id!!,
            title = saved.title,
            contents = saved.contents,
            weather = weather,
            user = UserResponse(user.id!!, user.email)
        )
    }

    fun getTodos(page: Int, size: Int): Page<TodoResponse> {
        val pageable = PageRequest.of(page - 1, size)

        return todoRepository.findAllByOrderByModifiedAtDesc(pageable)
            .map { todo ->
                TodoResponse(
                    id = todo.id!!,
                    title = todo.title,
                    contents = todo.contents,
                    weather = todo.weather,
                    user = UserResponse(todo.user.id!!, todo.user.email),
                    createdAt = todo.createdAt!!,
                    modifiedAt = todo.modifiedAt!!
                )
            }
    }

    fun searchTodos(
        page: Int,
        size: Int,
        weather: String?,
        start: LocalDateTime?,
        end: LocalDateTime?
    ): Page<TodoResponse> {
        val pageable = PageRequest.of(page - 1, size)

        return todoRepository.searchTodos(pageable, weather, start, end)
            .map { todo ->
                TodoResponse(
                    id = todo.id!!,
                    title = todo.title,
                    contents = todo.contents,
                    weather = todo.weather,
                    user = UserResponse(todo.user.id!!, todo.user.email),
                    createdAt = todo.createdAt!!,
                    modifiedAt = todo.modifiedAt!!
                )
            }
    }

    fun getTodo(todoId: Long): TodoResponse {
        val todo = todoQueryRepository.findByIdWithUser(todoId)
            .orElseThrow { InvalidRequestException("Todo not found") }

        val user = todo.user

        return TodoResponse(
            id = todo.id!!,
            title = todo.title,
            contents = todo.contents,
            weather = todo.weather,
            user = UserResponse(user.id!!, user.email),
            createdAt = todo.createdAt!!,
            modifiedAt = todo.modifiedAt!!
        )
    }

    fun searchTodosWithFilters(
        keyword: String?,
        nickname: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<TodoSummaryResponse> {
        return todoQueryRepository.searchTodosWithFilters(keyword, nickname, startDate, endDate, pageable)
    }
}
