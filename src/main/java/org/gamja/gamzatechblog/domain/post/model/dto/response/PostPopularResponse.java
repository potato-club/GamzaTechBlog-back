package org.gamja.gamzatechblog.domain.post.model.dto.response;

public record PostPopularResponse(
	Long postId,
	String title,
	String writer,
	String writerProfileImageUrl
) {
}
