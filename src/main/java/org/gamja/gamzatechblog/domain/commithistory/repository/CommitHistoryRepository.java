package org.gamja.gamzatechblog.domain.commithistory.repository;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.commithistory.model.entity.CommitHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitHistoryRepository {
	Optional<CommitHistory> findByPostId(Long postId);
}
