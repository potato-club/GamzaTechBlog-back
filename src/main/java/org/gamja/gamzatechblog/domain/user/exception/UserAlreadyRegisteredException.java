package org.gamja.gamzatechblog.domain.user.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class UserAlreadyRegisteredException extends BusinessException {
	public UserAlreadyRegisteredException(ErrorCode errorCode) {
		super(errorCode);
	}

}
