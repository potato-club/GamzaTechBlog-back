package org.gamja.gamzatechblog.domain.profileimage.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProfileImageSizeExceededException extends BusinessException {
	public ProfileImageSizeExceededException(ErrorCode errorCode) {
		super(errorCode);
	}
}
