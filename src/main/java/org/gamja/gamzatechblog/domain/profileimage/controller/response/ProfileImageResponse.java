package org.gamja.gamzatechblog.domain.profileimage.controller.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileImageResponse {
	private final Long id;

	private final String imageUrl;
}
