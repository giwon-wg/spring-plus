package org.example.expert.domain.todo.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.example.expert.domain.todo.dto.response.TodoSummaryResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoQueryRepository {

	Optional<Todo> findByIdWithUser(Long todoId);

	Page<TodoSummaryResponse> searchTodosWithFilters(String keyword, String nickname, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
