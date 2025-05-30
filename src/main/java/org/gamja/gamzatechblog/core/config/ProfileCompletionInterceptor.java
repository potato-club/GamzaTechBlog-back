package org.gamja.gamzatechblog.core.config;

import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.gamja.gamzatechblog.domain.user.validator.UserValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ProfileCompletionInterceptor implements HandlerInterceptor {

	private final UserService userService;
	private final UserValidator userValidator;

	public ProfileCompletionInterceptor(UserService userService,
		UserValidator userValidator) {
		this.userService = userService;
		this.userValidator = userValidator;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()
			&& !"anonymousUser".equals(auth.getPrincipal())) {
			String githubId = auth.getName();
			User user = userService.findByGithubId(githubId);
			userValidator.validateProfileComplete(user);
		}
		return true;
	}
}
