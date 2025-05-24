package org.gamja.gamzatechblog.domain.post.validator;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.domain.post.exception.PostAccessDeniedException;
import org.gamja.gamzatechblog.domain.post.exception.PostNotFoundException;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.repository.PostRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostValidator {

	private final PostRepository postRepository;

	public Post validatePostExists(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));
	}

	public void validateOwnership(Post post, User currentUser) {
		if (!post.getUser().getId().equals(currentUser.getId())) {
			throw new PostAccessDeniedException(ErrorCode.HANDLE_ACCESS_DENIED);
		}
	}
}