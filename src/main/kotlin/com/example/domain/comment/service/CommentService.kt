package com.example.domain.comment.service

import com.example.domain.user.dto.response.UserResponse
import com.example.domain.user.entity.User
import lombok.RequiredArgsConstructor
import com.example.domain.comment.dto.request.CommentSaveRequest
import com.example.domain.comment.dto.response.CommentResponse
import com.example.domain.comment.dto.response.CommentSaveResponse
import com.example.domain.comment.entity.Comment
import com.example.domain.comment.repository.CommentRepository
import com.example.domain.common.dto.AuthUser
import com.example.domain.common.exception.InvalidRequestException
import com.example.domain.todo.repository.TodoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val todoRepository: TodoRepository,
    private val commentRepository: CommentRepository
) {

    @Transactional
    fun saveComment(authUser: AuthUser, todoId: Long, commentSaveRequest: CommentSaveRequest): CommentSaveResponse {
        val user = User.fromAuthUser(authUser)
        val todo = todoRepository.findById(todoId)
            .orElseThrow { InvalidRequestException("Todo not found") }

        val newComment = Comment(
            contents = commentSaveRequest.contents,
            user = user,
            todo = todo
        )

        val savedComment = commentRepository.save(newComment)

        return CommentSaveResponse(
            id = savedComment.id!!,
            contents = savedComment.contents,
            user = UserResponse(user.id!!, user.email)
        )
    }

    fun getComments(todoId: Long): List<CommentResponse> {
        return commentRepository.findByTodoIdWithUser(todoId).map { comment ->
            val user = comment.user
            CommentResponse(
                id = comment.id!!,
                contents = comment.contents,
                user = UserResponse(user.id!!, user.email)
            )
        }
    }
}
