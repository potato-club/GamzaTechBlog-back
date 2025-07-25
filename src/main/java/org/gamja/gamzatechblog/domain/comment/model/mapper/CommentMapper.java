package org.gamja.gamzatechblog.domain.comment.model.mapper;

import java.util.stream.Collectors;

import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentListResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
	default CommentResponse mapToCommentTree(Comment comment) {
		return CommentResponse.builder()
			.commentId(comment.getId())
			.writer(comment.getUser().getNickname())
			.writerProfileImageUrl(
				comment.getUser().getProfileImage() != null
					? comment.getUser().getProfileImage().getProfileImageUrl()
					: null
			)
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.replies(
				comment.getReplies().stream()
					.map(this::mapToCommentTree)
					.collect(Collectors.toList())
			)
			.build();
	}

	@Mapping(source = "id", target = "commentId")
	@Mapping(source = "content", target = "content")
	@Mapping(source = "createdAt", target = "createdAt")
	@Mapping(source = "post.id", target = "postId")
	@Mapping(source = "post.title", target = "postTitle")
	CommentListResponse toCommentListResponse(Comment comment);
}
