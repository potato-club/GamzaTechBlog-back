package org.gamja.gamzatechblog.core.error.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;

public class GithubOAuthTokenInvalidException extends BusinessException {
	public GithubOAuthTokenInvalidException(ErrorCode errorCode) {
		super(errorCode);
	}
}
