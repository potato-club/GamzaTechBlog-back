package org.gamja.gamzatechblog.domain.user.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.core.auth.oauth.model.entity.GithubOauthToken;
import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.project.model.entity.Project;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.user.model.type.Position;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(name = "email", length = 100, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "role", length = 20, nullable = false)
	private UserRole role = UserRole.PRE_REGISTER;

	@Column(name = "gamja_batch")
	private Integer gamjaBatch;

	@Column(name = "student_number")
	private String studentNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "position", length = 20)
	private Position position;

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
	private GitHubRepo githubRepo;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Project> projects = new ArrayList<>();

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private ProfileImage profileImage;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private GithubOauthToken oauthToken;

	@Builder
	public User(String githubId, String name, String email, String studentNumber, Integer gamjaBatch, String nickname,
		Position position) {
		this.githubId = githubId;
		this.name = name;
		this.email = email;
		this.studentNumber = studentNumber;
		this.gamjaBatch = gamjaBatch;
		this.nickname = nickname;
		this.position = position;
	}

	public void setNickname(String newNickname) {
		this.nickname = newNickname;
	}

	public void setEmail(String newEmail) {
		this.email = newEmail;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public void setGamjaBatch(Integer gamjaBatch) {
		this.gamjaBatch = gamjaBatch;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setUserRole(UserRole role) {
		this.role = role;
	}

	public void setProfileImage(ProfileImage profileImage) {
		this.profileImage = profileImage;
		if (profileImage != null && profileImage.getUser() != this) {
			profileImage.setUser(this);
		}
	}

	public void changeProfileImage(ProfileImage newPi) {
		if (this.profileImage != null) {
			this.profileImage.setUser(null);
		}
		// 새 것 연결
		this.profileImage = newPi;
		if (newPi != null) {
			newPi.setUser(this);
		}
	}

	public boolean isProfileComplete() {
		return email != null && !email.isBlank()
			&& studentNumber != null && !studentNumber.isBlank()
			&& gamjaBatch != null
			&& position != null;
	}
}