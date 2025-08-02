package org.gamja.gamzatechblog.domain.user.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class UserNotPendingException extends BusinessException {
	public UserNotPendingException() {
		super(ErrorCode.INVALID_INPUT_VALUE, "사용자가 승인 대기 상태가 아닙니다.");
	}
}
