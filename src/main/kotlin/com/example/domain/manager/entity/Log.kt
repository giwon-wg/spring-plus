package com.example.domain.manager.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "log")
class Log(

    val action: String,

    val details: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    lateinit var createdAt: LocalDateTime

    @PrePersist // 초기화?
    fun prePersist() {
        this.createdAt = LocalDateTime.now()
    }
}
