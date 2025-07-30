package org.gamja.gamzatechblog.domain.project.model.entity;

import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(name = "thumbnail_url", length = 255)
	private String thumbnailUrl;

	public static Project newProject(User owner, String title, String description, String thumbnailUrl) {
		return Project.builder()
			.owner(owner)
			.title(title)
			.description(description)
			.thumbnailUrl(thumbnailUrl)
			.build();
	}

	public void change(String title, String description, String thumbnailUrl) {
		this.title = title;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
	}
}