package org.gamja.gamzatechblog.domain.commithistory.model.dto.response;

import java.time.LocalDateTime;

public record CommitHistoryResponse(
	String sha,
	String message,
	LocalDateTime createdAt
) {
}
