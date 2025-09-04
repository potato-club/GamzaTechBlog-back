package org.gamja.gamzatechblog.domain.introduction.service;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.introduction.model.dto.response.IntroResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface IntroService {
	IntroResponse create(User user, String content);

	void delete(Long introId, User user);

	PagedResponse<IntroResponse> list(Pageable pageable);

}
