package com.example.domain.comment.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import com.example.domain.comment.dto.request.CommentSaveRequest
import com.example.domain.comment.dto.response.CommentResponse
import com.example.domain.comment.dto.response.CommentSaveResponse
import com.example.domain.comment.service.CommentService
import com.example.domain.common.dto.AuthUser
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequiredArgsConstructor
class CommentController(

    private val commentService: CommentService
) {

    @PostMapping("/todos/{todoId}/comments")
    fun saveComment(
        @AuthenticationPrincipal authUser: AuthUser,
        @PathVariable todoId: Long,
        @RequestBody commentSaveRequest: @Valid CommentSaveRequest
    ): ResponseEntity<CommentSaveResponse> {
        return ResponseEntity.ok(commentService.saveComment(authUser, todoId, commentSaveRequest))
    }

    @GetMapping("/todos/{todoId}/comments")
    fun getComments(@PathVariable todoId: Long): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity.ok(commentService.getComments(todoId))
    }
}
