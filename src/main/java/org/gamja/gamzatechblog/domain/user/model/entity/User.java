package org.gamja.gamzatechblog.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.profile.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.project.model.entity.Project;
import org.gamja.gamzatechblog.domain.repository.model.entity.Repository;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "github_id", length = 100, nullable = false, unique = true)
    private String githubId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private UserRole role;

    @Column(name = "gamja_batch")
    private Integer gamjaBatch;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Repository repository;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    @Builder
    public User(String githubId, String name, String email, Integer gamjaBatch) {
        this.githubId   = githubId;
        this.name       = name;
        this.email      = email;
        this.role       = UserRole.USER;
        this.gamjaBatch = gamjaBatch;
    }
}