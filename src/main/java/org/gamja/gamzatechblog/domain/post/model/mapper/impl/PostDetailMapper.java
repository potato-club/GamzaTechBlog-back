package org.gamja.gamzatechblog.domain.post.model.mapper.impl;

import java.util.List;

import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
	componentModel = "spring",
	imports = java.util.stream.Collectors.class
)
public interface PostDetailMapper {

	@Mapping(target = "postId", source = "post.id")
	@Mapping(target = "writer", source = "post.user.nickname")
	@Mapping(
		target = "writerProfileImageUrl",
		expression = "java(post.getUser() != null && post.getUser().getProfileImage() != null "
			+ "? post.getUser().getProfileImage().getProfileImageUrl() "
			+ ": null)"
	)
	@Mapping(target = "title", source = "post.title")
	@Mapping(target = "content", source = "post.content")
	@Mapping(target = "tags", expression = "java(post.getPostTags().stream()  \n" +
		"    .map(pt -> pt.getTag().getTagName())  \n" +
		"    .collect(Collectors.toList()))")
	@Mapping(target = "createdAt", source = "post.createdAt")
	@Mapping(target = "updatedAt", source = "post.updatedAt")
	@Mapping(target = "comments", source = "comments")
	PostDetailResponse toDetailResponse(Post post, List<CommentResponse> comments);
}