package org.gamja.gamzatechblog.domain.comment.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class CommentAccessDeniedException extends BusinessException {
	public CommentAccessDeniedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
