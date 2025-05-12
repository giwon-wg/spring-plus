package com.example.domain.todo.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.example.domain.comment.entity.QComment
import com.example.domain.manager.entity.QManager
import com.example.domain.todo.dto.response.TodoSummaryResponse
import com.example.domain.todo.entity.QTodo
import com.example.domain.todo.entity.Todo
import com.example.domain.user.entity.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
class TodoQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : TodoQueryRepository {

    override fun findByIdWithUser(todoId: Long): Optional<Todo> {
        val todo = QTodo.todo
        val user = QUser.user

        return Optional.ofNullable(queryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne()
        )
    }

    override fun searchTodosWithFilters(
        keyword: String?,
        nickname: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<TodoSummaryResponse> {
        val todo = QTodo.todo
        val user = QUser.user
        val manager = QManager.manager
        val comment = QComment.comment

        val content = queryFactory
            .select(
                Projections.constructor(
                    TodoSummaryResponse::class.java,
                    todo.title,
                    manager.countDistinct(),
                    comment.count()
                )
            )
            .from(todo)
            .leftJoin(todo.managers, manager)
            .leftJoin(manager.user, user)
            .leftJoin(todo.comments, comment)
            .where(
                titleContains(keyword),
                nicknameContains(nickname),
                createdBetween(startDate, endDate)
            )
            .groupBy(todo.id)
            .orderBy(todo.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val count = queryFactory
            .select(todo.countDistinct())
            .from(todo)
            .leftJoin(todo.managers, manager)
            .leftJoin(manager.user, user)
            .where(
                titleContains(keyword),
                nicknameContains(nickname),
                createdBetween(startDate, endDate)
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, count)
    }

    private fun titleContains(keyword: String?): BooleanExpression? {
        return if (!keyword.isNullOrBlank()) QTodo.todo.title.contains(keyword) else null
    }

    private fun nicknameContains(nickname: String?): BooleanExpression? {
        return if (!nickname.isNullOrBlank()) QManager.manager.user.nickname.contains(nickname) else null
    }

    private fun createdBetween(start: LocalDate?, end: LocalDate?): BooleanExpression? {
        return if (start != null && end != null) {
            QTodo.todo.createdAt.between(start.atStartOfDay(), end.plusDays(1).atStartOfDay())
        }  else null
    }
}
