package org.gamja.gamzatechblog.domain.like.service.impl;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.like.model.mapper.LikeMapper;
import org.gamja.gamzatechblog.domain.like.service.LikeService;
import org.gamja.gamzatechblog.domain.like.service.port.LikeQueryPort;
import org.gamja.gamzatechblog.domain.like.service.port.LikeRepository;
import org.gamja.gamzatechblog.domain.like.validator.LikeValidator;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
	private final LikeQueryPort likeQueryPort;

	@Override
	@Transactional(readOnly = true)
	@Cacheable(
		value = "myLikes",
		key = "#user.id + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()"
	)
	public PagedResponse<LikeResponse> getMyLikes(User user, Pageable pageable) {
		return likeQueryPort.findMyLikesByUser(user, pageable);
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "postDetail", key = "#postId"),
		@CacheEvict(value = "myLikes", allEntries = true)
	})
	public LikeResponse likePost(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		likeValidator.validateNotAlreadyLiked(currentUser, post);
		Like like = Like.createByUserForPost(currentUser, post);
		Like saved = likeRepository.saveLike(like);
		return likeMapper.toLikeResponse(saved);
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "postDetail", key = "#postId"),
		@CacheEvict(value = "myLikes", allEntries = true)
	})
	public void unlikePost(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		likeValidator.validateExists(currentUser, post);
		likeRepository.deleteByUserAndPost(currentUser, post);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isPostLiked(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		return likeRepository.existsByUserAndPost(currentUser, post);
	}
}
