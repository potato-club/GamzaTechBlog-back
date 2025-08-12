package org.gamja.gamzatechblog.domain.comment.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
	@Schema(description = "댓글 내용", example = "글 감사합니다.")
	@NotBlank(message = "댓글 내용은 비어 있을 수 없습니다")
	@Size(max = 1000, message = "댓글 내용은 최대 1000자까지 가능합니다")
	String content,
	Long parentCommentId
) {
}
