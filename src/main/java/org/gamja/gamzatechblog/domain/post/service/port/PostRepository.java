package org.gamja.gamzatechblog.domain.post.service.port;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {
	/**
 * Retrieves a post by its unique identifier.
 *
 * @param postId the unique ID of the post to retrieve
 * @return an {@code Optional} containing the post if found, or empty if no post exists with the given ID
 */
Optional<Post> findById(Long postId);

	/**
 * Retrieves a paginated list of posts authored by the specified user, ordered by creation date in descending order.
 *
 * @param user the user whose posts are to be retrieved
 * @param pageable pagination information for the result set
 * @return a page of posts authored by the user, sorted by most recent first
 */
Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	/**
 * Returns the total number of posts authored by the specified user.
 *
 * @param user the user whose posts are to be counted
 * @return the count of posts created by the given user
 */
int countByUser(User user);

	/**
 * Saves the given Post entity, creating a new record or updating an existing one.
 *
 * @param post the Post entity to be saved
 * @return the persisted Post entity
 */
	Post save(Post post);

	/**
 * Deletes the specified post from the repository.
 *
 * @param post the post entity to be removed
 */
void delete(Post post);
}
