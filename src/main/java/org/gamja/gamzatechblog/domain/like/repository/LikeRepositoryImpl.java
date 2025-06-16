package org.gamja.gamzatechblog.domain.like.repository;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.entity.QLike;
import org.gamja.gamzatechblog.domain.post.model.entity.QPost;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LikeRepositoryImpl implements LikeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public PagedResponse<LikeResponse> findMyLikesByUser(User user, Pageable pageable) {
		QLike like = QLike.like;
		QPost post = QPost.post;

		long total = queryFactory
			.select(like.count())
			.from(like)
			.where(like.user.eq(user))
			.fetchOne();

		List<LikeResponse> content = queryFactory
			.select(Projections.fields(
				LikeResponse.class,
				like.id.as("likeId"),
				post.id.as("postId"),
				post.title.as("title"),
				Expressions.stringTemplate(
					"substring(cast({0} as char), 1, 100)",
					post.content
				).as("contentSnippet"),
				post.user.nickname.as("writer"),
				like.createdAt.as("createdAt")
			))
			.from(like)
			.join(like.post, post)
			.where(like.user.eq(user))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(like.createdAt.desc())
			.fetch();

		int totalPages = (int)Math.ceil((double)total / pageable.getPageSize());

		return new PagedResponse<>(
			content,
			pageable.getPageNumber(),
			pageable.getPageSize(),
			total,
			totalPages
		);
	}
}
