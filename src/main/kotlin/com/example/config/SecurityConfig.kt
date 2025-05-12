package com.example.config

import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CSRF(사이트 간 위조 요청) 보안 비활성화 - JWT 기반에서는 필요 없음
            .csrf { it.disable() }

            // 세션을 사용하지 않음 - JWT 사용
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // 요청별 인가 규칙
            .authorizeHttpRequests {
                it
                    .requestMatchers("/auth/**").permitAll() // /auth/** 경로는 누구나 접근 가능
                    .requestMatchers("/admin/**").hasRole("ADMIN") // /admin/** 경로는 어드민만 접근 가능
                    .anyRequest().authenticated()  // 그 외 모든 요청은 인증 필요
            }

            // JWT 인증 필터 등록 (기본 인증 필터 앞에 위치)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

            // 기본 로그인 폼 사용 안함 - JWT 사용
            .formLogin { it.disable() }

            // HTTP Basic 인증 사용 안 함 (ID/PW를 매 요청마다 보내는 방식)
            .httpBasic { it.disable() }

        // 최종 보안 설정을 적용한 SecurityFilterChain 반환
        return http.build()
    }
}
