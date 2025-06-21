package org.gamja.gamzatechblog.domain.post.infrastructure.jpa;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpaRepository extends JpaRepository<Post, Long> {
	/**
	 * Retrieves a Post by its ID, eagerly loading the associated user and tags.
	 *
	 * @param postId the unique identifier of the Post
	 * @return an Optional containing the Post with its user and tags if found, or empty if not found
	 */
	@EntityGraph(attributePaths = {"user", "postTags", "postTags.tag"})
	Optional<Post> findById(Long postId);

	/**
 * Retrieves a paginated list of posts authored by the specified user, ordered by creation date in descending order.
 *
 * @param user the user whose posts are to be retrieved
 * @param pageable pagination information
 * @return a page of posts authored by the user, sorted by most recent first
 */
Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	/**
 * Returns the number of posts authored by the specified user.
 *
 * @param user the user whose posts are to be counted
 * @return the total number of posts associated with the user
 */
int countByUser(User user);
}
