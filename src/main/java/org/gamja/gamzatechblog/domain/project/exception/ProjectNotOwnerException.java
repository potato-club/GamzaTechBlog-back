package org.gamja.gamzatechblog.domain.project.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProjectNotOwnerException extends BusinessException {
	public ProjectNotOwnerException(ErrorCode errorCode) {
		super(errorCode);
	}
}
