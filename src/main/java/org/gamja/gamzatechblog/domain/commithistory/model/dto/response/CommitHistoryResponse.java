package org.gamja.gamzatechblog.domain.commithistory.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommitHistoryResponse {
	private final String sha;
	private final String message;
	private final LocalDateTime createdAt;
}

