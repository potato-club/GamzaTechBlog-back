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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gamja.gamzatechblog.domain.like.model.entity.QLike;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.service.port.PostQueryPort;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
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
		BooleanExpression where = (filterTags != null && !filterTags.isEmpty())
			? tag.tagName.in(filterTags)
			: null;
		return fetchPosts(pageable, where);
	}

	@Override
	public Page<PostListResponse> findMyPosts(Pageable pageable, User currentUser) {
		BooleanExpression where = post.user.eq(currentUser);
		return fetchPosts(pageable, where);
	}

	private Page<PostListResponse> fetchPosts(Pageable pageable, BooleanExpression where) {
		// 1) 페이징용 ID 조회
		List<Long> ids = queryFactory
			.select(post.id)
			.from(post)
			.leftJoin(post.postTags, postTag)
			.leftJoin(postTag.tag, tag)
			.where(where)
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		if (ids.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, 0);
		}

		// 2) fetch join 으로 N+1 방지
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

		// 3) 썸네일 이미지 URL만 별도 조회
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
			firstImageByPost.putIfAbsent(
				t.get(postIdPath),
				t.get(postImage.postImageUrl)
			);
		}

		// 4) DTO 매핑
		List<PostListResponse> content = posts.stream()
			.map(p -> {
				PostListResponse dto = new PostListResponse();
				dto.setPostId(p.getId());
				dto.setTitle(p.getTitle());
				String c = p.getContent();
				dto.setContentSnippet(c == null ? "" :
					c.length() <= 100 ? c : c.substring(0, 100) + "...");
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
				dto.setThumbnailImageUrl(firstImageByPost.get(p.getId()));
				return dto;
			})
			.toList();

		// 5) 전체 카운트
		Long total = queryFactory
			.select(post.countDistinct())
			.from(post)
			.leftJoin(post.postTags, postTag)
			.leftJoin(postTag.tag, tag)
			.where(where)
			.fetchOne();

		return new PageImpl<>(
			content,
			pageable,
			total == null ? 0L : total
		);
	}

	@Override
	public List<Post> findWeeklyPopularPosts(LocalDateTime since, int limit) {
		List<Long> ids = queryFactory
			.select(post.id)
			.from(post)
			.leftJoin(post.likes, QLike.like)
			.where(post.createdAt.after(since))
			.groupBy(post.id)
			.orderBy(QLike.like.count().desc(), post.createdAt.desc())
			.limit(limit)
			.fetch();

		if (ids.isEmpty()) {
			return List.of();
		}

		List<Post> posts = queryFactory
			.selectDistinct(post)
			.from(post)
			.leftJoin(post.postTags, postTag).fetchJoin()
			.leftJoin(postTag.tag, tag).fetchJoin()
			.leftJoin(post.user, user).fetchJoin()
			.leftJoin(user.profileImage, profileImage).fetchJoin()
			.where(post.id.in(ids))
			.fetch();

		Map<Long, Post> map = posts.stream()
			.collect(Collectors.toMap(Post::getId, Function.identity()));
		return ids.stream()
			.map(map::get)
			.toList();
	}
}
