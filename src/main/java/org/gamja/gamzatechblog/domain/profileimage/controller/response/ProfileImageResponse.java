package org.gamja.gamzatechblog.domain.profileimage.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileImageResponse {
	private final Long id;

	private final String imageUrl;
}
