package com.example.domain.comment.entity

import com.example.domain.user.entity.User
import jakarta.persistence.*
import com.example.domain.common.entity.Timestamped
import com.example.domain.todo.entity.Todo

@Entity
@Table(name = "comments")
class Comment(

    @Column(nullable = false)
    var contents: String,

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var user: User,

    @JoinColumn(name = "todo_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var todo: Todo
) : Timestamped() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
