package org.gamja.gamzatechblog.core.error.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
