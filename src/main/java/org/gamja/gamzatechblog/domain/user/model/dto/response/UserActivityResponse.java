package org.gamja.gamzatechblog.domain.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserActivityResponse {
	private int likedPostCount;
	private int writtenPostCount;
	private int writtenCommentCount;
}
