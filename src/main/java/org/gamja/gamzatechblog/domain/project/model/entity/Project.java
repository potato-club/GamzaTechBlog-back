package org.gamja.gamzatechblog.domain.project.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

/**
 * Project: 사용자가 수행한 프로젝트 정보(제목, 설명, 소요 기간)를 저장하는 엔티티
 */
@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Project extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "duration", length = 50)
    private String duration;
}