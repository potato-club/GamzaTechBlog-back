package org.gamja.gamzatechblog.core.error.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;

public class UnauthorizedException extends BusinessException {
	public UnauthorizedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
