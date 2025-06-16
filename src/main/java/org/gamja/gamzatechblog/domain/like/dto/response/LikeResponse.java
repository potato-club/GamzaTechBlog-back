package org.gamja.gamzatechblog.domain.like.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LikeResponse {
	private Long id;
	private Long postId;
	private LocalDateTime createdAt;
}

