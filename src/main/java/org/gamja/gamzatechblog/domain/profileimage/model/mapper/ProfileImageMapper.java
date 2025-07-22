package org.gamja.gamzatechblog.domain.profileimage.model.mapper;

import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileImageMapper {

	@Mapping(source = "info.profileImageUrl", target = "profileImageUrl")
	@Mapping(source = "user", target = "user")
	ProfileImage fromOAuthUserInfo(OAuthUserInfo info, User user);

	@Mapping(source = "user", target = "user")
	@Mapping(source = "imageUrl", target = "profileImageUrl")
	ProfileImage toProfileImage(User user, String imageUrl);
}
