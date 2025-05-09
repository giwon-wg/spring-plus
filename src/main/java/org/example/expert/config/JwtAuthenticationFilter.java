package org.example.expert.config;

import java.io.IOException;
import java.util.List;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException, IOException {

		String token = jwtUtil.resolveToken(request); // 요청 헤더에서 토큰값 추출

		if (token != null & jwtUtil.validateToken(token)) { // 토큰이 존재하고, 유효성 검증메서드를 통과 할 경우
			Claims claims = jwtUtil.extractClaims(token); //토큰에서 클레임(정보) 추출

			Long userId = Long.parseLong(claims.getSubject()); // 사용자 ID를 클레임에서 꺼냄
			String email = claims.get("email", String.class); // 사용자 email를 클레임에서 꺼냄
			String role = claims.get("userRole", String.class); // 사용자 role을 클레임에서 꺼냄
			String nickname = claims.get("nickname", String.class); //사용자 nickname을 클레임에서 꺼냄

			AuthUser authUser = new AuthUser(userId, email, UserRole.of(role), nickname);
			List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role)); // Spring Security에서 사용할 권한 정보 생성

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authUser, null, authorities); // 인증 객체 생성 / 비밀번호는 null

			SecurityContextHolder.getContext().setAuthentication(authenticationToken); // SecurityContext에 인증 객체 저장
			System.out.println(role);
			System.out.println(authorities);
			System.out.println(authenticationToken);
		}
		filterChain.doFilter(request, response); // 다음 필터로 요청 전달
	}
}
