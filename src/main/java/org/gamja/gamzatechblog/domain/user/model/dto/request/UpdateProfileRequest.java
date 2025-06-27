package org.gamja.gamzatechblog.domain.user.model.dto.request;

import org.gamja.gamzatechblog.domain.user.model.type.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileRequest {

	private String email;

	private String studentNumber;

	private Integer gamjaBatch;

	private Position position;
}
