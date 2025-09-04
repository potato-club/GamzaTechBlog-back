package org.gamja.gamzatechblog.domain.introduction.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class IntroException extends BusinessException {
	public IntroException(ErrorCode errorCode) {
		super(errorCode);
	}

	public static IntroException alreadyExists() {
		return new IntroException(ErrorCode.INTRO_ALREADY_EXISTS);
	}

	public static IntroException notFound() {
		return new IntroException(ErrorCode.INTRO_NOT_FOUND);
	}

	public static IntroException deleteForbidden() {
		return new IntroException(ErrorCode.INTRO_DELETE_FORBIDDEN);
	}
}
