package org.gamja.gamzatechblog.domain.user.model.dto.request;

import org.gamja.gamzatechblog.domain.user.model.type.Position;

import lombok.Getter;

@Getter
public class UpdateProfileRequest {

	private String email;

	private String studentNumber;

	private Integer gamjaBatch;

	private Position position;
}
