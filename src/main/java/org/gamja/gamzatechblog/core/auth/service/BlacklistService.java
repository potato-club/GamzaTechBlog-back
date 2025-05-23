package org.gamja.gamzatechblog.core.auth.service;

public interface BlacklistService {
	void blacklistTokens(String githubId);
}
