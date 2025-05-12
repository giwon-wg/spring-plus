package com.example.domain.todo.controller

import jakarta.validation.Valid
import com.example.domain.common.dto.AuthUser
import com.example.domain.todo.dto.request.TodoSaveRequest
import com.example.domain.todo.dto.response.TodoResponse
import com.example.domain.todo.dto.response.TodoSaveResponse
import com.example.domain.todo.dto.response.TodoSummaryResponse
import com.example.domain.todo.service.TodoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/todos")
class TodoController(
    private val todoService: TodoService

) {
    @PostMapping
    fun saveTodo(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestBody @Valid request: TodoSaveRequest
    ): ResponseEntity<TodoSaveResponse> {
        return ResponseEntity.ok(todoService.saveTodo(authUser, request))
    }

    @GetMapping
    fun getTodos(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<TodoResponse>> {
        return ResponseEntity.ok(todoService.getTodos(page, size))
    }

    @GetMapping("/search")
    fun searchTodos(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) weather: String?,
        @RequestParam(required = false) start: String?,
        @RequestParam(required = false) end: String?
    ): ResponseEntity<Page<TodoResponse>> {
        val startDateTime = start?.let { LocalDateTime.parse(it) }
        val endDateTime = end?.let { LocalDateTime.parse(it) }
        return ResponseEntity.ok(todoService.searchTodos(page, size, weather, startDateTime, endDateTime))
    }

    @GetMapping("/{todoId}")
    fun getTodo(@PathVariable todoId: Long): ResponseEntity<TodoResponse> {
        return ResponseEntity.ok(todoService.getTodo(todoId))
    }

    @GetMapping("/searchFilters")
    fun searchTodosWithFilters(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?,
        @RequestParam(required = false) nickname: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TodoSummaryResponse>> {
        val result = todoService.searchTodosWithFilters(keyword, nickname, startDate, endDate, pageable)
        return ResponseEntity.ok(result)
    }
}
