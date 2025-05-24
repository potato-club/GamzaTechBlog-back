package org.gamja.gamzatechblog.domain.post.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class PostAccessDeniedException extends BusinessException {
	public PostAccessDeniedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
