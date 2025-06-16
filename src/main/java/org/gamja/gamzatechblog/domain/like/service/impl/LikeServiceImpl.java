package org.gamja.gamzatechblog.domain.like.service.impl;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.like.model.mapper.LikeMapper;
import org.gamja.gamzatechblog.domain.like.repository.LikeRepository;
import org.gamja.gamzatechblog.domain.like.service.LikeService;
import org.gamja.gamzatechblog.domain.like.validator.LikeValidator;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {
	private final LikeRepository likeRepository;
	private final LikeMapper likeMapper;
	private final PostValidator postValidator;
	private final LikeValidator likeValidator;

	// @Override
	// @Transactional(readOnly = true)
	// public List<LikeResponse> getMyLikes(User currentUser) {
	// 	return likeRepository.findByUserOrderByCreatedAtDesc(currentUser)
	// 		.stream()
	// 		.map(likeMapper::toLikeResponse)
	// 		.collect(Collectors.toList());
	// }

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<LikeResponse> getMyLikes(User currentUser, Pageable pageable) {
		Page<Like> page = likeRepository.findByUser(currentUser, pageable);
		Page<LikeResponse> dtoPage = page.map(likeMapper::toLikeResponse);
		return PagedResponse.pagedFrom(dtoPage);
	}

	@Override
	@Transactional
	public LikeResponse likePost(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		likeValidator.validateNotAlreadyLiked(currentUser, post);
		Like like = Like.createByUserForPost(currentUser, post);
		Like saved = likeRepository.save(like);
		return likeMapper.toLikeResponse(saved);
	}

	@Override
	@Transactional
	public void unlikePost(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		likeValidator.validateExists(currentUser, post);
		likeRepository.deleteByUserAndPost(currentUser, post);
	}
}
