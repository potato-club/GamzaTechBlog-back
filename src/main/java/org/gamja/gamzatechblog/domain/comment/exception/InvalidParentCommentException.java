package org.gamja.gamzatechblog.domain.comment.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class InvalidParentCommentException extends BusinessException {
	public InvalidParentCommentException(ErrorCode errorCode) {
		super(errorCode);
	}
}
