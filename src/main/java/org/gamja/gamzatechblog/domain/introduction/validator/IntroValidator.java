package org.gamja.gamzatechblog.domain.introduction.validator;

import org.gamja.gamzatechblog.domain.introduction.exception.IntroException;
import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.introduction.service.port.IntroductionRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IntroValidator {

	private final IntroductionRepository introductionRepository;

	// 자기소개 중복 검사
	public void validateNotExists(User user) {
		if (introductionRepository.existsByUser(user)) {
			throw IntroException.alreadyExists();
		}
	}

	// 자기소개 존재 여부 확인
	public Introduction validateExists(Long introId) {
		return introductionRepository.findById(introId)
			.orElseThrow(IntroException::notFound);
	}

	// 삭제 권한 확인
	public void validateOwner(Introduction intro, User user) {
		if (!intro.getUser().getId().equals(user.getId())) {
			throw IntroException.deleteForbidden();
		}
	}
}
