package org.gamja.gamzatechblog.core.gemini.dto.chat.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageRequest {

	@NotBlank(message = "메시지는 비어 있을 수 없습니다.")
	@Size(max = 2000, message = "메시지는 2000자를 초과할 수 없습니다.")
	private String message;
}
