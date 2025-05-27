package org.gamja.gamzatechblog.domain.tag.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.posttag.model.entity.PostTag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Tag: 게시물에 붙일 수 있는 태그 이름을 저장하는 엔티티
 */
@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Tag extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id;

	@Column(name = "tag_name", length = 100, nullable = false, unique = true)
	private String tagName;

	@OneToMany(mappedBy = "tag", orphanRemoval = true)
	@Builder.Default
	private List<PostTag> postTags = new ArrayList<>();
}
