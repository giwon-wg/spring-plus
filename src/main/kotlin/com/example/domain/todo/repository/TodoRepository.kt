package com.example.domain.todo.repository

import com.example.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TodoRepository : JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    fun findAllByOrderByModifiedAtDesc(pageable: Pageable): Page<Todo>

    @Query(
        value = """
        SELECT t
        FROM Todo t LEFT JOIN FETCH t.user u
        WHERE (:weather IS NULL OR t.weather = :weather)
            AND (:start IS NULL OR t.modifiedAt >= :start)
            AND (:end IS NULL OR t.modifiedAt <= :end)
        ORDER BY t.modifiedAt DESC
    """,
    countQuery = """
        SELECT COUNT(t)
        FROM Todo t
        WHERE (:weather IS NULL OR t.weather = :weather)
            AND (:start IS NULL OR t.modifiedAt >= :start)
            AND (:end IS NULL OR t.modifiedAt <= :end)
    """
    )
    fun searchTodos(
        pageable: Pageable,
        @Param("weather") weather: String?,
        @Param("start") start: LocalDateTime?,
        @Param("end") end: LocalDateTime?
    ): Page<Todo>
}
