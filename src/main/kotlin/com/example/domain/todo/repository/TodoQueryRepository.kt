package com.example.domain.todo.repository

import com.example.domain.todo.dto.response.TodoSummaryResponse
import com.example.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.*

interface TodoQueryRepository {
    fun findByIdWithUser(todoId: Long): Optional<Todo>

    fun searchTodosWithFilters(
        keyword: String?,
        nickname: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<TodoSummaryResponse>
}
