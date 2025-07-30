package org.gamja.gamzatechblog.domain.project.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProjectNotFoundException extends BusinessException {
	public ProjectNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
