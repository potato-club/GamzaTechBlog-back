package org.gamja.gamzatechblog.domain.post.infrastructure.adapter;

import static org.gamja.gamzatechblog.domain.post.model.entity.QPost.*;
import static org.gamja.gamzatechblog.domain.postimage.model.entity.QPostImage.*;
import static org.gamja.gamzatechblog.domain.posttag.model.entity.QPostTag.*;
import static org.gamja.gamzatechblog.domain.profileimage.model.entity.QProfileImage.*;
import static org.gamja.gamzatechblog.domain.tag.model.entity.QTag.*;
import static org.gamja.gamzatechblog.domain.user.model.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gamja.gamzatechblog.domain.like.model.entity.QLike;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.service.port.PostQueryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class PostQueryAdapter implements PostQueryPort {

	private final JPAQueryFactory queryFactory;

	public PostQueryAdapter(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<PostListResponse> findAllPosts(Pageable pageable, List<String> filterTags) {
		List<Long> ids = queryFactory
			.select(post.id)
			.from(post)
			.leftJoin(post.postTags, postTag)
			.leftJoin(postTag.tag, tag)
			.where(filterTags != null && !filterTags.isEmpty()
				? tag.tagName.in(filterTags)
				: null)
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		if (ids.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, 0);
		}

		List<Post> posts = queryFactory
			.selectDistinct(post)
			.from(post)
			.leftJoin(post.postTags, postTag).fetchJoin()
			.leftJoin(postTag.tag, tag).fetchJoin()
			.leftJoin(post.user, user).fetchJoin()
			.leftJoin(user.profileImage, profileImage).fetchJoin()
			.where(post.id.in(ids))
			.orderBy(post.createdAt.desc())
			.fetch();

		NumberPath<Long> postIdPath = post.id;
		List<Tuple> imageTuples = queryFactory
			.select(postIdPath, postImage.postImageUrl)
			.from(post)
			.leftJoin(post.images, postImage)
			.where(postIdPath.in(ids))
			.orderBy(postIdPath.asc(), postImage.id.asc())
			.fetch();

		Map<Long, String> firstImageByPost = new LinkedHashMap<>();
		for (Tuple t : imageTuples) {
			Long pid = t.get(postIdPath);
			String url = t.get(postImage.postImageUrl);
			firstImageByPost.putIfAbsent(pid, url);
		}

		List<PostListResponse> content = posts.stream()
			.map(p -> {
				PostListResponse dto = new PostListResponse();
				dto.setPostId(p.getId());
				dto.setTitle(p.getTitle());
				String c = p.getContent();
				dto.setContentSnippet(c.length() <= 100
					? c
					: c.substring(0, 100) + "...");
				dto.setWriter(p.getUser().getNickname());
				dto.setWriterProfileImageUrl(
					p.getUser().getProfileImage() != null
						? p.getUser().getProfileImage().getProfileImageUrl()
						: null
				);
				dto.setCreatedAt(p.getCreatedAt());
				dto.setTags(p.getPostTags().stream()
					.map(pt -> pt.getTag().getTagName())
					.toList());
				dto.setThumbnailImageUrl(
					firstImageByPost.get(p.getId())
				);
				return dto;
			})
			.toList();

		Long total = queryFactory
			.select(post.countDistinct())
			.from(post)
			.leftJoin(post.postTags, postTag)
			.leftJoin(postTag.tag, tag)
			.where(filterTags != null && !filterTags.isEmpty()
				? tag.tagName.in(filterTags)
				: null)
			.fetchOne();

		return new PageImpl<>(
			content,
			pageable,
			total == null ? 0L : total
		);
	}

	@Override
	public List<Post> findWeeklyPopularPosts(LocalDateTime since, int limit) {
		var likeCount = QLike.like.count();
		return queryFactory
			.select(post)
			.from(post)
			.leftJoin(post.likes, QLike.like).fetchJoin()
			.where(post.createdAt.after(since))
			.groupBy(post)
			.orderBy(likeCount.desc(), post.createdAt.desc())
			.limit(limit)
			.fetch();
	}
}
