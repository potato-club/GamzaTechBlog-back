package org.gamja.gamzatechblog.domain.like.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class AlreadyLikedException extends BusinessException {
	public AlreadyLikedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
