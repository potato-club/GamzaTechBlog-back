package org.gamja.gamzatechblog.core.error.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;

public class OAuthException extends BusinessException {
	public OAuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
