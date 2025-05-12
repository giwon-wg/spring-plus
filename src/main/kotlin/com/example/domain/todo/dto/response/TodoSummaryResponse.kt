package com.example.domain.todo.dto.response

import com.querydsl.core.annotations.QueryProjection

data class TodoSummaryResponse @QueryProjection constructor(

    val title: String,

    val managerCount: Long,

    val commentCount: Long
)
