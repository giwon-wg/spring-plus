package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record TodoSummaryResponse (

	String title,
	Long managerCount,
	Long commentCount
) {
	@QueryProjection
	public TodoSummaryResponse(String title, Long managerCount, Long commentCount) {
		this.title = title;
		this.managerCount = managerCount;
		this.commentCount = commentCount;
	}
}
