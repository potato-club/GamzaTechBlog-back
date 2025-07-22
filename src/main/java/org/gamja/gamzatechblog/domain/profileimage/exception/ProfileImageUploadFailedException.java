package org.gamja.gamzatechblog.domain.profileimage.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProfileImageUploadFailedException extends BusinessException {
	public ProfileImageUploadFailedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
