package org.gamja.gamzatechblog.domain.user.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProfileIncompleteException extends BusinessException {
	public ProfileIncompleteException(ErrorCode errorCode) {
		super(errorCode);
	}
}
