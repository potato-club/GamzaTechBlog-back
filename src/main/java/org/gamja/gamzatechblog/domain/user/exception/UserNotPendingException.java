package org.gamja.gamzatechblog.domain.user.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class UserNotPendingException extends BusinessException {
	public UserNotPendingException() {
		super(ErrorCode.INVALID_INPUT_VALUE);
	}
}
