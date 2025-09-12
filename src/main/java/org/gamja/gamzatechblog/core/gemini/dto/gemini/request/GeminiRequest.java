package org.gamja.gamzatechblog.core.gemini.dto.gemini.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeminiRequest {

	@JsonProperty("system_instruction")
	private Content systemInstruction;

	private List<Content> contents = new ArrayList<>();

	public static GeminiRequest fromSystemAndUser(String system, String userText) {
		GeminiRequest geminiRequest = new GeminiRequest();
		geminiRequest.systemInstruction = Content.fromText(system);
		geminiRequest.contents.add(Content.fromText(userText));
		return geminiRequest;
	}

	@Getter
	@NoArgsConstructor
	public static class Content {
		private List<Part> parts = new ArrayList<>();

		public static Content fromText(String text) {
			Content content = new Content();
			content.parts.add(new Part(text));
			return content;
		}
	}

	@Getter
	@NoArgsConstructor
	public static class Part {
		private String text;

		public Part(String text) {
			this.text = text;
		}
	}
}
