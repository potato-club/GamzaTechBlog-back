package org.gamja.gamzatechblog.domain.repository.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

/**
 * Repository: 사용자(User) 개인 레포지토리 정보(이름, URL)를 저장하는 엔티티
 */
@Entity
@Table(name = "repository")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
//깃허브 레포지토리입니다.
public class Repository extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repository_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "github_url", length = 255)
    private String githubUrl;
}
