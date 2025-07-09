package org.gamja.gamzatechblog.domain.like.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.entity.QLike;
import org.gamja.gamzatechblog.domain.like.service.port.LikeQueryPort;
import org.gamja.gamzatechblog.domain.post.model.entity.QPost;
import org.gamja.gamzatechblog.domain.posttag.model.entity.QPostTag;
import org.gamja.gamzatechblog.domain.tag.model.entity.QTag;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LikeQueryAdapter implements LikeQueryPort {
	private final JPAQueryFactory queryFactory;

	@Override
	public PagedResponse<LikeResponse> findMyLikesByUser(User user, Pageable pageable) {
		long total = countLikes(user);
		List<Tuple> rows = fetchLikeTuples(user, pageable);
		List<LikeResponse> content = mapTuplesToResponses(rows);

		int totalPages = (int)Math.ceil((double)total / pageable.getPageSize());
		return new PagedResponse<>(
			content,
			pageable.getPageNumber(),
			pageable.getPageSize(),
			total,
			totalPages
		);
	}

	private long countLikes(User user) {
		QLike like = QLike.like;
		return queryFactory
			.select(like.count())
			.from(like)
			.where(like.user.eq(user))
			.fetchOne();
	}

	private List<Tuple> fetchLikeTuples(User user, Pageable pageable) {
		QLike like = QLike.like;
		QPost post = QPost.post;
		QPostTag pt = QPostTag.postTag;
		QTag tag = QTag.tag;

		return queryFactory
			.select(
				like.id,
				post.id,
				post.title,
				post.content,
				post.user.nickname,
				like.createdAt,
				tag.tagName
			)
			.from(like)
			.leftJoin(like.post, post)
			.leftJoin(post.postTags, pt)
			.leftJoin(pt.tag, tag)
			.where(like.user.eq(user))
			.orderBy(like.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private List<LikeResponse> mapTuplesToResponses(List<Tuple> rows) {
		QLike like = QLike.like;
		QPost post = QPost.post;
		QTag tag = QTag.tag;

		Map<Long, List<Tuple>> grouped = rows.stream()
			.collect(Collectors.groupingBy(t -> t.get(like.id)));

		return grouped.entrySet().stream()
			.map(entry -> {
				Long likeId = entry.getKey();
				List<Tuple> list = entry.getValue();
				Tuple first = list.get(0);

				String fullContent = first.get(post.content);
				String snippet = fullContent.length() > 100
					? fullContent.substring(0, 100)
					: fullContent;

				List<String> tags = list.stream()
					.map(t -> t.get(tag.tagName))
					.distinct()
					.toList();

				return new LikeResponse(
					likeId,
					first.get(post.id),
					first.get(post.title),
					snippet,
					first.get(post.user.nickname),
					first.get(like.createdAt),
					tags
				);
			})
			.collect(Collectors.toList());
	}
}
