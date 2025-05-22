package org.gamja.gamzatechblog.core.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessCheckAspect {

	private final JwtProvider jwtProvider;

	@Around("@annotation(org.gamja.gamzatechblog.core.aop.AccessCheck)")
	public Object checkAccessToken(ProceedingJoinPoint pjp) throws Throwable {
		ServletRequestAttributes attrs =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			throw new UnauthorizedException(ErrorCode.AUTHENTICATION_FAILED);
		}
		HttpServletRequest req = attrs.getRequest();

		String header = req.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			throw new UnauthorizedException(ErrorCode.JWT_NOT_FOUND);
		}
		String token = header.substring(7);

		if (!jwtProvider.validateAccessToken(token)) {
			throw new UnauthorizedException(ErrorCode.EXPIRED_JWT);
		}

		return pjp.proceed();
	}
}
