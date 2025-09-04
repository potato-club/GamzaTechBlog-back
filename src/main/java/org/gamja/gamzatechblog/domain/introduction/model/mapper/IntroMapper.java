package org.gamja.gamzatechblog.domain.introduction.model.mapper;

import org.gamja.gamzatechblog.domain.introduction.model.dto.response.IntroResponse;
import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IntroMapper {

	@Mapping(source = "id", target = "introId")
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.nickname", target = "nickname")
	@Mapping(source = "user.profileImage.profileImageUrl", target = "profileImageUrl")
	@Mapping(source = "content", target = "content")
	@Mapping(source = "createdAt", target = "createdAt")
	IntroResponse toResponse(Introduction intro);

	default Introduction newIntroduction(User user, String content) {
		return Introduction.builder()
			.user(user)
			.content(content)
			.build();
	}
}
