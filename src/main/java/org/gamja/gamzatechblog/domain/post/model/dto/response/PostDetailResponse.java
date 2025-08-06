package org.gamja.gamzatechblog.domain.post.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;

public record PostDetailResponse(
	Long postId,
	String title,
	String content,
	String writer,
	String writerProfileImageUrl,
	int likesCount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	List<String> tags,
	List<CommentResponse> comments
) {
}
