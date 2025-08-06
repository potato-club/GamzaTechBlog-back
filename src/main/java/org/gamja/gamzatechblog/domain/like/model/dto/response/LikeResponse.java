package org.gamja.gamzatechblog.domain.like.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record LikeResponse(
	Long likeId,
	Long postId,
	String title,
	String contentSnippet,
	String writer,
	String writerProfileImageUrl,
	String thumbnailImageUrl,
	LocalDateTime createdAt,
	List<String> tags
) {
}
