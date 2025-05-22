package org.gamja.gamzatechblog.domain.user.model.mapper;

import org.gamja.gamzatechblog.domain.user.model.dto.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.UserProfileDto;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
	@Mapping(source = "profileImage.url", target = "profileImageUrl")
	@Mapping(source = "role", target = "role")
	@Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
	@Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
	UserProfileDto toUserProfileDto(User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void applyProfileUpdates(UpdateProfileRequest dto, @MappingTarget User user);
}
