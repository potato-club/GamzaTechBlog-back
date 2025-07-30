package org.gamja.gamzatechblog.domain.project.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class ProjectImageEmptyException extends BusinessException {
	public ProjectImageEmptyException(ErrorCode errorCode) {
		super(errorCode);
	}
}
