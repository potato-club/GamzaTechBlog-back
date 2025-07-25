package org.gamja.gamzatechblog.domain.post.model.mapper.impl;

import java.util.stream.Collectors;

import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {Collectors.class})
public interface PostListMapper {

	@Mapping(target = "postId", source = "id")
	@Mapping(target = "writer", source = "user.nickname")
	@Mapping(target = "title", source = "title")
	@Mapping(target = "contentSnippet", expression = "java(createSnippet(post.getContent()))")
	@Mapping(target = "tags", expression = "java(post.getPostTags().stream()  \n"
		+ "    .map(pt -> pt.getTag().getTagName())  \n"
		+ "    .collect(Collectors.toList()))")
	@Mapping(target = "createdAt", source = "createdAt")
	@Mapping(target = "writerProfileImageUrl",
		expression = "java(post.getUser().getProfileImage()!=null "
			+ "? post.getUser().getProfileImage().getProfileImageUrl() : null)")
	@Mapping(target = "thumbnailImageUrl",
		expression = "java(pickThumbnail(post))")
	PostListResponse toListResponse(Post post);

	default String createSnippet(String content) {
		if (content == null)
			return "";
		return content.length() <= 100
			? content
			: content.substring(0, 100) + "...";
	}

	default String pickThumbnail(Post post) {
		return post.getImages().stream()
			.findFirst()
			.map(PostImage::getPostImageUrl)
			.orElse(null);
	}
}
