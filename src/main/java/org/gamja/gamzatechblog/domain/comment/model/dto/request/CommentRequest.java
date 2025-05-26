package org.gamja.gamzatechblog.domain.comment.model.dto.request;

import lombok.Getter;

@Getter
public class CommentRequest {
	private String content;
	private Long parentCommentId;
}