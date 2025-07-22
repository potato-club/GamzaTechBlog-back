package org.gamja.gamzatechblog.domain.profileimage.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProfileImageDeleteFailedException extends BusinessException {
	public ProfileImageDeleteFailedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
