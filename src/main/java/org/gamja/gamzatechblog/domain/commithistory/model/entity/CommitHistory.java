package org.gamja.gamzatechblog.domain.commithistory.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.repository.model.entity.Repository;

/**
 * CommitHistory: 깃허브 레포지토리(Repository)와 연관된 커밋 이력을 저장하는 엔티티
 */
@Entity
@Table(name = "commit_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommitHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commit_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "commit_sha", length = 255, nullable = false)
    private String commitSha;

    @Column(name = "commit_message", length = 255)
    private String commitMessage;
}