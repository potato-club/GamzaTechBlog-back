package org.gamja.gamzatechblog.domain.introduction;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IntroductionRepositoryImpl implements IntroductionRepository {

	private final IntroductionJpaRepository introductionJpaRepository;

	@Override
	public boolean existsByUser(User user) {
		return introductionJpaRepository.existsByUser(user);
	}

	@Override
	public Optional<Introduction> findByUser(User user) {
		return introductionJpaRepository.findByUser(user);
	}

	@Override
	public Optional<Introduction> findById(Long introId) {
		return introductionJpaRepository.findById(introId);
	}

	@Override
	public Optional<Introduction> findByUserId(Long userId) {
		return introductionJpaRepository.findByUser_Id(userId);
	}

	@Override
	public List<Introduction> findAllOrderByCreatedDesc() {
		return introductionJpaRepository.findAllOrderByCreatedDesc();
	}

	@Override
	public Introduction save(Introduction intro) {
		return introductionJpaRepository.save(intro);
	}

	@Override
	public void delete(Introduction intro) {
		introductionJpaRepository.delete(intro);
	}

	@Override
	public Page<Introduction> findAll(Pageable pageable) {
		return introductionJpaRepository.findAll(pageable);
	}
}