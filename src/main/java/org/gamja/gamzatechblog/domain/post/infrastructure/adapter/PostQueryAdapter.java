package org.gamja.gamzatechblog.domain.post.infrastructure.adapter;

import static org.gamja.gamzatechblog.domain.post.model.entity.QPost.*;
import static org.gamja.gamzatechblog.domain.posttag.model.entity.QPostTag.*;
import static org.gamja.gamzatechblog.domain.tag.model.entity.QTag.*;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.service.port.PostQueryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class PostQueryAdapter implements PostQueryPort {

	private final JPAQueryFactory queryFactory;

	public PostQueryAdapter(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Post> findAllPosts(Pageable pageable, List<String> filterTags) {
		JPAQuery<Post> contentQuery = queryFactory
			.selectFrom(post)
			.distinct()
			.leftJoin(post.postTags, postTag).fetchJoin()
			.leftJoin(postTag.tag, tag).fetchJoin();

		if (filterTags != null && !filterTags.isEmpty()) {
			contentQuery.where(tag.tagName.in(filterTags));
		}

		contentQuery
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<Post> content = contentQuery.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(post.countDistinct())
			.from(post)
			.leftJoin(post.postTags, postTag)
			.leftJoin(postTag.tag, tag);

		if (filterTags != null && !filterTags.isEmpty()) {
			countQuery.where(tag.tagName.in(filterTags));
		}

		Long totalCount = countQuery.fetchOne();
		long total = totalCount != null ? totalCount : 0L;

		return new PageImpl<>(content, pageable, total);
	}
}
