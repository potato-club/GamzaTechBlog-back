package org.gamja.gamzatechblog.domain.introduction;

import java.util.List;

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

	List<IntroResponse> toResponseList(List<Introduction> list);
}
