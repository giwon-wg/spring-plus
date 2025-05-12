package com.example.config

import com.example.domain.common.exception.ServerException
import com.example.domain.user.enums.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.util.*

@Component
class JwtUtil {
    @Value("\${jwt.secret.key}")
    private lateinit var secretKey: String
    private lateinit var key: Key
    private val signatureAlgorithm = SignatureAlgorithm.HS256

    @PostConstruct
    fun init() {
        val bytes = Base64.getDecoder().decode(secretKey)
        key = Keys.hmacShaKeyFor(bytes)
    }

    fun createToken(userId: Long, email: String, userRole: UserRole, nickname: String): String {
        val date = Date()

        return BEARER_PREFIX +
                Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("email", email)
                    .claim("userRole", userRole)
                    .claim("nickname", nickname)
                    .setExpiration(Date(date.time + TOKEN_TIME))
                    .setIssuedAt(date) // 발급일
                    .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                    .compact()
    }

    fun substringToken(tokenValue: String): String {
        return if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            tokenValue.substring(BEARER_PREFIX.length)
        } else {
            throw ServerException("Not Found Token")
        }
    }

    fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader("Authorization") // 토큰값 헤더에서 가져오기
        return if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) { // 값이 있고, bearer 로 시작한다면
            return bearer.substring(BEARER_PREFIX.length) // 앞의 "bearer "(7자리)를 때고 토큰값만 리턴 할것
        } else null
    }

    fun validateToken(token: String?): Boolean {
        return try {
            if (token != null) extractClaims(token) != null else false
        } catch (e: Exception) {
            false // 예외 발생 시 유효하지 않은 토큰으로 판단
        }
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_TIME = 60 * 60 * 1000L // 60분
    }
}
