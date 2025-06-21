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

	/**
	 * Retrieves a post by its unique identifier.
	 *
	 * @param postId the ID of the post to retrieve
	 * @return an {@code Optional} containing the post if found, or empty if not present
	 */
	@Override
	public Optional<Post> findById(Long postId) {
		return postJpaRepository.findById(postId);
	}

	/**
	 * Retrieves a paginated list of posts for the specified user, ordered by creation date in descending order.
	 *
	 * @param user the user whose posts are to be retrieved
	 * @param pageable pagination information
	 * @return a page of posts belonging to the user, sorted by most recent first
	 */
	@Override
	public Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
		return postJpaRepository.findByUserOrderByCreatedAtDesc(user, pageable);
	}

	/**
	 * Returns the number of posts associated with the specified user.
	 *
	 * @param user the user whose posts are to be counted
	 * @return the count of posts belonging to the user
	 */
	@Override
	public int countByUser(User user) {
		return postJpaRepository.countByUser(user);
	}

	/**
	 * Persists the given Post entity and returns the saved instance.
	 *
	 * @param post the Post entity to be saved
	 * @return the persisted Post entity
	 */
	@Override
	public Post save(Post post) {
		return postJpaRepository.save(post);
	}

	/**
	 * Deletes the specified post entity from the data store.
	 *
	 * @param post the post entity to be deleted
	 */
	@Override
	public void delete(Post post) {
		postJpaRepository.delete(post);
	}
}
