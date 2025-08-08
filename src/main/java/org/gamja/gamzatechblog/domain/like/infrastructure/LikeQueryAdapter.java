package org.gamja.gamzatechblog.domain.like.infrastructure;

import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LikeQueryAdapter implements LikeQueryPort {
	@Value("${like.in-clause-limit:500}")
	private int inClauseLimit;

	private final JPAQueryFactory queryFactory;
	private final PostUtil postUtil;

	@PostConstruct
	private void validateInClauseLimit() {
		Assert.isTrue(inClauseLimit > 0,
			"like.in-clause-limit은 양수여야 합니다. 현재 값: " + inClauseLimit);
	}

	@Override
	public PagedResponse<LikeResponse> findMyLikesByUser(User user, Pageable pageable) {
		List<Long> likeIds = fetchLikeIds(user, pageable);
		Map<Long, List<Tuple>> tuplesById = fetchTuplesByLikeIds(user, likeIds);
		List<LikeResponse> content = convertGroupedTuplesToResponses(tuplesById, likeIds);
		long total = queryFactory
			.select(QLike.like.id.count())
			.from(QLike.like)
			.where(QLike.like.user.eq(user))
			.fetchOne();

		return PagedResponse.pagedFrom(
			new PageImpl<>(content, pageable, total)
		);
	}

	private List<Long> fetchLikeIds(User user, Pageable pageable) {
		QLike like = QLike.like;
		return queryFactory
			.select(like.id)
			.from(like)
			.where(like.user.eq(user))
			.orderBy(like.createdAt.desc(), like.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private Map<Long, List<Tuple>> fetchTuplesByLikeIds(User user, List<Long> likeIds) {
		if (likeIds.isEmpty()) {
			return Map.of();
		}

		QLike like = QLike.like;
		QPost post = QPost.post;
		QUser qUser = QUser.user;
		QProfileImage pi = QProfileImage.profileImage;
		QPostTag pt = QPostTag.postTag;
		QTag tag = QTag.tag;
		QPostImage postImage = QPostImage.postImage;

		Map<Long, List<Tuple>> resultMap = new LinkedHashMap<>();

		for (int i = 0; i < likeIds.size(); i += inClauseLimit) {
			List<Long> chunk = likeIds.subList(i, Math.min(i + inClauseLimit, likeIds.size()));
			List<Tuple> fetched = queryFactory
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
				.where(like.user.eq(user)
					.and(like.id.in(chunk)))
				.orderBy(like.createdAt.desc(), like.id.asc())
				.fetch();

			Map<Long, List<Tuple>> grouped = fetched.stream()
				.collect(Collectors.groupingBy(
					t -> t.get(like.id),
					LinkedHashMap::new,
					Collectors.toList()
				));

			grouped.forEach((id, list) ->
				resultMap.computeIfAbsent(id, key -> new ArrayList<>()).addAll(list)
			);
		}

		return resultMap;
	}

	private List<LikeResponse> convertGroupedTuplesToResponses(Map<Long, List<Tuple>> grouped, List<Long> likeIds) {
		return likeIds.stream()
			.filter(grouped::containsKey)
			.map(id -> buildLikeResponse(id, grouped.get(id)))
			.collect(Collectors.toList());
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
