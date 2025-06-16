package org.gamja.gamzatechblog.domain.like.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class LikeNotFoundException extends BusinessException {
	public LikeNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
