package org.example.expert.domain.todo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSummaryResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {
		QTodo todo = QTodo.todo;
		QUser user = QUser.user;

		return Optional.ofNullable(
			queryFactory
				.selectFrom(todo)
				.leftJoin(todo.user, user).fetchJoin()
				.where(todo.id.eq(todoId))
				.fetchOne()
		);
	}

	@Override
	public Page<TodoSummaryResponse> searchTodosWithFilters(String keyword, String nickname, LocalDate startDate,
		LocalDate endDate, Pageable pageable) {
		QTodo todo = QTodo.todo;
		QUser user = QUser.user;
		QManager manager = QManager.manager;
		QComment comment = QComment.comment;

		List<TodoSummaryResponse> content = queryFactory
			.select(Projections.constructor(
				TodoSummaryResponse.class,
				todo.title,
				manager.countDistinct(),
				comment.count()
			))
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
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(todo.countDistinct())
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(manager.user, user)
			.where(
				titleContains(keyword),
				nicknameContains(nickname),
				createdBetween(startDate, endDate)
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, count);
	}

	private BooleanExpression titleContains(String keyword) {
		return StringUtils.hasText(keyword) ? QTodo.todo.title.contains(keyword) : null;
	}

	private BooleanExpression nicknameContains(String nickname) {
		return StringUtils.hasText(nickname) ? QManager.manager.user.nickname.contains(nickname) : null;
	}

	private BooleanExpression createdBetween(LocalDate start, LocalDate end) {
		if (start != null && end != null) {
			return QTodo.todo.createdAt.between(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
		}
		return null;
	}
}
