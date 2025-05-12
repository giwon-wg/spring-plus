package com.example.domain.user.entity

import jakarta.persistence.*
import lombok.Getter
import lombok.NoArgsConstructor
import com.example.domain.common.dto.AuthUser
import com.example.domain.common.entity.Timestamped
import com.example.domain.user.enums.UserRole
import lombok.AccessLevel

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", indexes = [Index(name = "idx_nickname", columnList = "nickname")])
class User(

    @Column(unique = true, nullable = false)
    var email: String,

    var password: String? = null,

    @Enumerated(EnumType.STRING)
    var userRole: UserRole,

    @Column(nullable = false)
    var nickname: String,

    var profileImage: String? = null

) : Timestamped() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    fun changePassword(password: String?) {
        this.password = password
    }

    fun updateRole(userRole: UserRole) {
        this.userRole = userRole
    }

    companion object {
        fun fromAuthUser(authUser: AuthUser): User {
            return User(
                email = authUser.email,
                userRole = authUser.userRole,
                nickname = authUser.nickname
            ).apply {
                this.id = authUser.id
            }
        }
    }
}
