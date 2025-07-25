package org.gamja.gamzatechblog.domain.like.infrastructure;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.entity.QLike;
import org.gamja.gamzatechblog.domain.like.service.port.LikeQueryPort;
import org.gamja.gamzatechblog.domain.post.model.entity.QPost;
import org.gamja.gamzatechblog.domain.postimage.model.entity.QPostImage;
import org.gamja.gamzatechblog.domain.posttag.model.entity.QPostTag;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.QProfileImage;
import org.gamja.gamzatechblog.domain.tag.model.entity.QTag;
import org.gamja.gamzatechblog.domain.user.model.entity.QUser;
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
		QUser qUser = QUser.user;
		QProfileImage profileImage = QProfileImage.profileImage;
		QPostTag pt = QPostTag.postTag;
		QTag tag = QTag.tag;
		QPostImage postImage = QPostImage.postImage;

		return queryFactory
			.select(
				like.id,
				post.id,
				post.title,
				post.content,
				post.user.nickname,
				profileImage.profileImageUrl,
				postImage.id,            // 이미지 ID
				postImage.postImageUrl,  // 이미지 URL
				like.createdAt,
				tag.tagName
			)
			.from(like)
			.leftJoin(like.post, post)
			.leftJoin(post.user, qUser)
			.leftJoin(qUser.profileImage, profileImage)
			.leftJoin(post.postTags, pt)
			.leftJoin(pt.tag, tag)
			.leftJoin(post.images, postImage)   // 포스트 → 이미지 조인
			.where(like.user.eq(user))
			.orderBy(like.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private List<LikeResponse> mapTuplesToResponses(List<Tuple> rows) {
		QLike like = QLike.like;
		QPost post = QPost.post;
		QProfileImage profileImage = QProfileImage.profileImage;
		QTag tag = QTag.tag;
		QPostImage postImage = QPostImage.postImage;

		// Like ID별로 묶기
		Map<Long, List<Tuple>> grouped = rows.stream()
			.collect(Collectors.groupingBy(t -> t.get(like.id)));

		return grouped.entrySet().stream()
			.map(entry -> {
				Long likeId = entry.getKey();
				List<Tuple> list = entry.getValue();

				// 좋아요당 첫 번째(가장 작은 ID) 이미지를 thumbnail로 선택
				String thumbnail = list.stream()
					.filter(t -> t.get(postImage.postImageUrl) != null)
					.min(Comparator.comparing(t -> t.get(postImage.id)))
					.map(t -> t.get(postImage.postImageUrl))
					.orElse(null);

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
					first.get(profileImage.profileImageUrl),
					thumbnail,
					first.get(like.createdAt),
					tags
				);
			})
			.toList();
	}
}
