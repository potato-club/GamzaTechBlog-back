package org.gamja.gamzatechblog.domain.post.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class PostNotFoundException extends BusinessException {
	public PostNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
