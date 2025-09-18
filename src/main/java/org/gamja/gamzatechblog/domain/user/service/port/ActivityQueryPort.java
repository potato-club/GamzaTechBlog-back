package org.gamja.gamzatechblog.domain.user.service.port;

import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface ActivityQueryPort {
	UserActivityResponse getActivitySummary(User user);
}
