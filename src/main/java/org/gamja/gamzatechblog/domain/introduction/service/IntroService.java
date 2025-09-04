package org.gamja.gamzatechblog.domain.introduction;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface IntroService {
	IntroResponse create(User user, String content);

	List<IntroResponse> getIntros();

	IntroResponse getMine(User user);

	IntroResponse getByUserId(Long userId);

	void delete(Long introId, User user);

	PagedResponse<IntroResponse> list(Pageable pageable);

}
