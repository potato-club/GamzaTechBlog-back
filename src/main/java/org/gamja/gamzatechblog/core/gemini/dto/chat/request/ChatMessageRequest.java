package org.gamja.gamzatechblog.core.gemini.dto.chat.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageRequest {

	@NotBlank(message = "메시지는 비어 있을 수 없습니다.")
	private String message;
}
