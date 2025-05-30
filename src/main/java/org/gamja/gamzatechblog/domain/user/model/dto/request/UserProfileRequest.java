package org.gamja.gamzatechblog.domain.user.model.dto.request;

import org.gamja.gamzatechblog.domain.user.model.type.Position;

import lombok.Getter;

@Getter
public class UserProfileRequest {

	private String email;

	private String studentNumber;

	private Integer gamjaBatch;

	private Position position;

}
