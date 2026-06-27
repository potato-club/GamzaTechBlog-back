package org.gamja.gamzatechblog.core.auth.service;

public interface BlacklistService {
	String BLACKLIST_ACCESS_PREFIX = "blacklist:access:";

	void blacklistTokens(String githubId);
}
