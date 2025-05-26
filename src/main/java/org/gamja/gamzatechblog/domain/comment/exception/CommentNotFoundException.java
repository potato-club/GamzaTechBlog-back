package org.gamja.gamzatechblog.domain.comment.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class CommentNotFoundException extends BusinessException {
	public CommentNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
