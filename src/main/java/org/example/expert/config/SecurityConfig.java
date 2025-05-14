package org.example.expert.config;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // CSRF 비활성화 (JWT 사용)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/**").permitAll() // 인증 없이 허용
				.requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN 권한 필요
				.anyRequest().authenticated() // 나머지 요청은 인증 필요
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
				.accessDeniedHandler((req, res, e) -> res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
