package org.example.expert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable) // CSRF(사이트 간 위조 요청) 보안 비활성화 - JWT 기반에서는 필요 없음
			.sessionManagement(session -> session // 세션을 사용하지 않음 - JWT 사용
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth // 요청별 인가 규칙
				.requestMatchers("/auth/**").permitAll() // /auth/** 경로는 누구나 접근 가능
				.requestMatchers("/admin/**").hasRole("ADMIN") // /admin/** 경로는 어드민 만 접근 가능
				.anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 등록 (기본 인증 필터 앞에 위치)
			.formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 사용 안함 - JWT 사용
			.httpBasic(AbstractHttpConfigurer::disable); // HTTP Basic 인증 사용 안 함 (ID/PW를 매 요청마다 보내는 방식)

		//최종 보안 설정을 적용한 SecurityFilterChain 반환
		return http.build();
	}
}
