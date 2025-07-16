package org.gamja.gamzatechblog.common.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PagedResponse<T>(
	List<T> content,
	int page,
	int size,
	long totalElements,
	int totalPages
) {
	public static <T> PagedResponse<T> pagedFrom(Page<T> pageData) {
		return new PagedResponse<>(
			pageData.getContent(),
			pageData.getNumber(),
			pageData.getSize(),
			pageData.getTotalElements(),
			pageData.getTotalPages()
		);
	}

	public static <E, R> PagedResponse<R> of(Page<E> pageData, Function<E, R> converter) {
		List<R> content = pageData.getContent().stream()
			.map(converter)
			.toList();

		return new PagedResponse<>(
			content,
			pageData.getNumber(),
			pageData.getSize(),
			pageData.getTotalElements(),
			pageData.getTotalPages()
		);
	}
}
