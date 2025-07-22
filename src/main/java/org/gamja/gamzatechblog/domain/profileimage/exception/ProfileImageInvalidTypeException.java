package org.gamja.gamzatechblog.domain.profileimage.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProfileImageInvalidTypeException extends BusinessException {
	public ProfileImageInvalidTypeException(ErrorCode errorCode) {
		super(errorCode);
	}
}
