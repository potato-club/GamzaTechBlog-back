package org.gamja.gamzatechblog.domain.user.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class DuplicateValueException extends BusinessException {
	public DuplicateValueException(ErrorCode errorCode) {
		super(errorCode);
	}
}
