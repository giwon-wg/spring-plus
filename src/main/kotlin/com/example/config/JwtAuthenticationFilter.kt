package com.example.config

import com.example.domain.common.dto.AuthUser
import com.example.domain.user.enums.UserRole
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
@RequiredArgsConstructor
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtUtil.resolveToken(request)

        if (token != null && jwtUtil.validateToken(token)) {
            val claims = jwtUtil.extractClaims(token)

            val userId = claims.subject.toLong()
            val email = claims.get("email", String::class.java)
            val role = claims.get("userRole", String::class.java)
            val nickname = claims.get("nickname", String::class.java)

            val authUser = AuthUser(userId, email, UserRole.of(role), nickname)
            val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

            val authenticationToken =
                UsernamePasswordAuthenticationToken(authUser, null, authorities)

            SecurityContextHolder.getContext().authentication = authenticationToken
        }

        filterChain.doFilter(request, response)
    }
}
