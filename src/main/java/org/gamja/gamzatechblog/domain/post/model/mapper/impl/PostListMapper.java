package org.gamja.gamzatechblog.domain.post.model.mapper.impl;

import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
	componentModel = "spring",
	imports = java.util.stream.Collectors.class   // ← 이 라인 추가
)
public interface PostListMapper {

	@Mapping(target = "postId", source = "id")
	@Mapping(target = "writer", source = "user.nickname")
	@Mapping(target = "title", source = "title")
	@Mapping(target = "contentSnippet", expression = "java(createSnippet(post.getContent()))")
	@Mapping(target = "tags", expression = "java(post.getPostTags().stream()  \n"
		+ "    .map(pt -> pt.getTag().getTagName())  \n" + "    .collect(Collectors.toList()))")
	@Mapping(target = "createdAt", source = "createdAt")
	PostListResponse toListResponse(Post post);

	//미리보기 100글자 이하
	default String createSnippet(String content) {
		if (content == null)
			return "";
		return content.length() <= 100
			? content
			: content.substring(0, 100) + "...";
	}
}
