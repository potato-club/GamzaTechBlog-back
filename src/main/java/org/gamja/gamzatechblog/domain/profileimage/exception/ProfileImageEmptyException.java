package org.gamja.gamzatechblog.domain.profileimage.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProfileImageEmptyException extends BusinessException {
	public ProfileImageEmptyException(ErrorCode errorCode) {
		super(errorCode);
	}
}
