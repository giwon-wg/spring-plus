package com.example.aop

import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDateTime

private val logger = KotlinLogging.logger{}

@Aspect
@Component
class AdminAccessLoggingAspect (
    private val request: HttpServletRequest
) {

    @Before("execution(* com.example.domain.user.controller.UserController.getUser(..))")
    fun logAfterChangeUserRole(joinPoint: JoinPoint) {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request

        val userId = request.getAttribute("userId")?.toString() ?: "unknown"
        val requestUrl = request.requestURI
        val requestTime = LocalDateTime.now()

        logger.info {
            "Admin Access Log - userId=$userId, time=$requestTime, uri=$requestUrl, method=${joinPoint.signature.name}"
        }
    }
}
