package org.gamja.gamzatechblog.domain.user.model.mapper;

import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
	@Mapping(source = "profileImage.profileImageUrl", target = "profileImageUrl")
	@Mapping(source = "role", target = "role")
	@Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
	@Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
	UserProfileResponse toUserProfileResponse(User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void applyProfileUpdates(UpdateProfileRequest dto, @MappingTarget User user);

	void completeProfile(UserProfileRequest dto, @MappingTarget User user);
}
