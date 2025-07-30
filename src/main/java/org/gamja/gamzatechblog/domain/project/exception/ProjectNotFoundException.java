package org.gamja.gamzatechblog.domain.project.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProjectException extends BusinessException {
	public ProjectException(ErrorCode errorCode) {
		super(errorCode);
	}
}
