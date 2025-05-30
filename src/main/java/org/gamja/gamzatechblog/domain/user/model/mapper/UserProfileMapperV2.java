package org.gamja.gamzatechblog.domain.user.model.mapper;

import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapperV2 {

	void updateFromDto(UserProfileRequest dto, @MappingTarget User user);

	UserProfileResponse toResponse(User user);
}
