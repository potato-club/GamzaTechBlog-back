package org.gamja.gamzatechblog.domain.like.infrastructure;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.entity.QLike;
import org.gamja.gamzatechblog.domain.like.service.port.LikeQueryPort;
import org.gamja.gamzatechblog.domain.post.model.entity.QPost;
import org.gamja.gamzatechblog.domain.post.util.PostUtil;
import org.gamja.gamzatechblog.domain.postimage.model.entity.QPostImage;
import org.gamja.gamzatechblog.domain.posttag.model.entity.QPostTag;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.QProfileImage;
import org.gamja.gamzatechblog.domain.tag.model.entity.QTag;
import org.gamja.gamzatechblog.domain.user.model.entity.QUser;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LikeQueryAdapter implements LikeQueryPort {

	private final JPAQueryFactory queryFactory;
	private final PostUtil postUtil;

	@Override
	public PagedResponse<LikeResponse> findMyLikesByUser(User user, Pageable pageable) {
		QueryResults<Tuple> results = fetchTuplesWithPaging(user, pageable);
		List<LikeResponse> content = convertTuplesToResponses(results.getResults());

		long totalElements = queryFactory
			.select(QLike.like.id.countDistinct())
			.from(QLike.like)
			.where(QLike.like.user.eq(user))
			.fetchOne();
		Page<LikeResponse> page = new PageImpl<>(content, pageable, totalElements);
		return PagedResponse.pagedFrom(page);
	}

	private QueryResults<Tuple> fetchTuplesWithPaging(User user, Pageable pageable) {
		QLike like = QLike.like;
		QPost post = QPost.post;
		QUser qUser = QUser.user;
		QProfileImage pi = QProfileImage.profileImage;
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
				pi.profileImageUrl,
				postImage.id,
				postImage.postImageUrl,
				like.createdAt,
				tag.tagName
			)
			.from(like)
			.leftJoin(like.post, post)
			.leftJoin(post.user, qUser)
			.leftJoin(qUser.profileImage, pi)
			.leftJoin(post.postTags, pt)
			.leftJoin(pt.tag, tag)
			.leftJoin(post.images, postImage)
			.where(like.user.eq(user))
			.orderBy(like.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
	}

	private List<LikeResponse> convertTuplesToResponses(List<Tuple> rows) {
		QLike like = QLike.like;
		QPost post = QPost.post;
		QProfileImage pi = QProfileImage.profileImage;
		QTag tag = QTag.tag;
		QPostImage postImage = QPostImage.postImage;

		Map<Long, List<Tuple>> grouped = rows.stream()
			.collect(Collectors.groupingBy(
				t -> t.get(like.id),
				LinkedHashMap::new,
				Collectors.toList()
			));

		return grouped.entrySet().stream()
			.map(entry -> buildLikeResponse(entry.getKey(), entry.getValue()))
			.toList();
	}

	private LikeResponse buildLikeResponse(Long likeId, List<Tuple> tuples) {
		QLike like = QLike.like;
		QPost post = QPost.post;
		QProfileImage pi = QProfileImage.profileImage;
		QTag tag = QTag.tag;
		QPostImage postImage = QPostImage.postImage;

		Tuple first = tuples.get(0);
		String snippet = postUtil.makeSnippet(first.get(post.content), 100);
		String thumbnail = tuples.stream()
			.filter(t -> t.get(postImage.postImageUrl) != null)
			.min(Comparator.comparing(t -> t.get(postImage.id)))
			.map(t -> t.get(postImage.postImageUrl))
			.orElse(null);
		List<String> tags = tuples.stream()
			.map(t -> t.get(tag.tagName))
			.distinct()
			.toList();

		return new LikeResponse(
			likeId,
			first.get(post.id),
			first.get(post.title),
			snippet,
			first.get(post.user.nickname),
			first.get(pi.profileImageUrl),
			thumbnail,
			first.get(like.createdAt),
			tags
		);
	}
}
