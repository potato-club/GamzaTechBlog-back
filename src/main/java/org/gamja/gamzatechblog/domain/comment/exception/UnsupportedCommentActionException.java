package org.gamja.gamzatechblog.domain.comment.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class UnsupportedCommentActionException extends BusinessException {
	public UnsupportedCommentActionException(ErrorCode errorCode) {
		super(errorCode);
	}
}
