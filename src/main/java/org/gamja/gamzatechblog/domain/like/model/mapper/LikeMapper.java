package org.gamja.gamzatechblog.domain.like.model.mapper;

import org.gamja.gamzatechblog.domain.like.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
	@Mapping(source = "post.id", target = "postId")
	LikeResponse toLikeResponse(Like like);
}
