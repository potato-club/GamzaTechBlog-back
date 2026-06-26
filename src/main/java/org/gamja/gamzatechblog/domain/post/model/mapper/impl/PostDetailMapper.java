package org.gamja.gamzatechblog.domain.post.model.mapper.impl;

import java.util.List;

import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostDetailMapper {

	@Mapping(target = "postId", source = "post.id")
	@Mapping(target = "writer", source = "post.user.nickname")
	@Mapping(target = "writerProfileImageUrl", expression = "java(writerProfileImageUrl(post))")
	@Mapping(target = "likesCount", expression = "java(post.getLikes().size())")
	@Mapping(target = "title", source = "post.title")
	@Mapping(target = "content", source = "post.content")
	@Mapping(target = "tags", expression = "java(tagNames(post))")
	@Mapping(target = "githubId", source = "post.user.githubId")
	@Mapping(target = "createdAt", source = "post.createdAt")
	@Mapping(target = "updatedAt", source = "post.updatedAt")
	@Mapping(target = "comments", source = "comments")
	PostDetailResponse toDetailResponse(Post post, List<CommentResponse> comments);

	default String writerProfileImageUrl(Post post) {
		if (post.getUser() == null || post.getUser().getProfileImage() == null) {
			return null;
		}
		return post.getUser().getProfileImage().getProfileImageUrl();
	}

	default List<String> tagNames(Post post) {
		return post.getPostTags().stream()
			.map(pt -> pt.getTag().getTagName())
			.toList();
	}
}