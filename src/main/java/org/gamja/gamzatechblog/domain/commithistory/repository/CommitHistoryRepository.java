package org.gamja.gamzatechblog.domain.commithistory.repository;

import java.util.List;

import org.gamja.gamzatechblog.domain.commithistory.model.entity.CommitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitHistoryRepository extends JpaRepository<CommitHistory, Long> {
	List<CommitHistory> findByPostId(Long postId);
}
