package org.gamja.gamzatechblog.domain.like.service.port;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface LikeQueryPort {
	PagedResponse<LikeResponse> findMyLikesByUser(User user, Pageable pageable);

}
