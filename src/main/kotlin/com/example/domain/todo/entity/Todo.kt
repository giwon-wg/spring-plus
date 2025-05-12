package com.example.domain.todo.entity

import com.example.domain.comment.entity.Comment
import com.example.domain.user.entity.User
import jakarta.persistence.*
import lombok.Getter
import lombok.NoArgsConstructor
import com.example.domain.common.entity.Timestamped
import com.example.domain.manager.entity.Manager

@Getter
@Entity
@NoArgsConstructor
@Table(name = "todos")
class Todo(

    var title: String,

    var contents: String,

    var weather: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User

) : Timestamped() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.REMOVE])
    val comments: MutableList<Comment> = mutableListOf()

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.ALL], orphanRemoval = true)
    val managers: MutableList<Manager> = mutableListOf()

    init {
        managers.add(Manager(user, this))
    }
}
