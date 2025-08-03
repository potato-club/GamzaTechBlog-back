package org.gamja.gamzatechblog.domain.user.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingUserResponse {
	private final Long userId;
	private final Integer gamjaBatch;
	private final String name;
	private final String position;
}
