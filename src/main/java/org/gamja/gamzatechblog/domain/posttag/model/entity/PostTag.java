package org.gamja.gamzatechblog.domain.posttag.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;

/**
 * PostTag: 게시물(Post)과 태그(Tag)의 다대다 관계를 관리하는 조인 엔티티
 */
@Entity
@Table(name = "post_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostTag extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id")
    private Tag tag;
}