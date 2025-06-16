package org.gamja.gamzatechblog.core.config;

import org.gamja.gamzatechblog.core.auth.jwt.JwtAuthenticationEntryPoint;
import org.gamja.gamzatechblog.core.auth.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationEntryPoint authenticationEntryPoint;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	private static final String[] PUBLIC = {
		"/oauth2/**",
		"/api/auth/reissue",
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/webjars/**",
		"/login/oauth2/code/**"
	};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.disable())
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.oauth2Login(o -> o
				.loginPage("/oauth2/authorization/github")
				.successHandler(oAuth2LoginSuccessHandler)
			)

			.exceptionHandling(e -> e
				.authenticationEntryPoint(authenticationEntryPoint)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers(PUBLIC).permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/users/me/complete")
				.hasRole("PRE_REGISTER")
				.requestMatchers(HttpMethod.GET,
					"/api/v1/tags",
					"/api/v1/posts",
					"/api/v1/posts/{id:[0-9]+}"
				).permitAll()
				.requestMatchers(HttpMethod.GET,
					"/api/v1/posts/me")
				.hasRole("USER")
				.requestMatchers(HttpMethod.GET, "/api/v1/users/me/role")
				.hasAnyRole("USER", "PRE_REGISTER")
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().hasRole("USER")
			)
			.addFilterAfter(
				jwtAuthenticationFilter,
				ExceptionTranslationFilter.class
			);

		return http.build();
	}
}
