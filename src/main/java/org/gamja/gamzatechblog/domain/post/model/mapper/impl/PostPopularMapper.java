package org.gamja.gamzatechblog.domain.post.model.mapper.impl;

import org.gamja.gamzatechblog.domain.post.model.dto.response.PostPopularResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostPopularMapper {

	@Mapping(target = "postId", source = "post.id")
	@Mapping(target = "title", source = "post.title")
	@Mapping(target = "writer", source = "post.user.nickname")
	@Mapping(
		target = "writerProfileImageUrl",
		expression = "java(post.getUser().getProfileImage()!=null "
			+ "? post.getUser().getProfileImage().getProfileImageUrl() "
			+ ": null)"
	)
	PostPopularResponse toPopularResponse(Post post);
}
