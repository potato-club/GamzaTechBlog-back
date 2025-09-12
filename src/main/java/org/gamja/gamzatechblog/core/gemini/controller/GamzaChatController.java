package org.gamja.gamzatechblog.core.gemini.controller;

import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.core.gemini.dto.ChatMessageRequest;
import org.gamja.gamzatechblog.core.gemini.dto.ChatMessageResponse;
import org.gamja.gamzatechblog.core.gemini.service.GamzaChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/ai")
@RequiredArgsConstructor
public class GamzaChatController {

	private final GamzaChatService gamzaChatService;

	@Operation(summary = "챗봇", tags = "챗봇 기능")
	@PostMapping("/chat")
	public ResponseEntity<ChatMessageResponse> chat(@RequestBody @Valid ChatMessageRequest request) {
		ChatMessageResponse response = gamzaChatService.getReply(request);
		return ResponseEntity.ok(response);
	}
}
