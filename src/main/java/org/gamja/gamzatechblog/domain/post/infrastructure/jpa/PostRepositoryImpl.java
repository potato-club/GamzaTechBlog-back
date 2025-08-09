package org.gamja.gamzatechblog.domain.post.infrastructure.jpa;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

	private final PostJpaRepository postJpaRepository;

	@Override
	public Optional<Post> findById(Long postId) {
		return postJpaRepository.findById(postId);
	}

	@Override
	public Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
		return postJpaRepository.findByUserOrderByCreatedAtDesc(user, pageable);
	}

	@Override
	public int countByUser(User user) {
		return postJpaRepository.countByUser(user);
	}

	@Override
	public Post save(Post post) {
		return postJpaRepository.save(post);
	}

	@Override
	public void delete(Post post) {
		postJpaRepository.delete(post);
	}

	@Override
	public void flush() {
		postJpaRepository.flush();
	}

}
