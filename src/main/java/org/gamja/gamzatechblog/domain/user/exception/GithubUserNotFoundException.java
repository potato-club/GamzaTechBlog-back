package org.gamja.gamzatechblog.domain.user.exception;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public class GithubUserNotFoundException extends BusinessException {
    public GithubUserNotFoundException(String githubId) {
        super(ErrorCode.GITHUB_USER_NOT_FOUND, githubId + "에 해당하는 깃허브 사용자를 찾을 수 없습니다.");
    }
}

