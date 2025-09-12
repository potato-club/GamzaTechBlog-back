package org.gamja.gamzatechblog.core.gemini.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeminiReqDto {

	@JsonProperty("system_instruction")
	private Content systemInstruction;

	private List<Content> contents = new ArrayList<>();

	public static GeminiReqDto fromSystemAndUser(String system, String userText) {
		GeminiReqDto dto = new GeminiReqDto();
		dto.systemInstruction = Content.fromText(system);
		dto.contents.add(Content.fromText(userText));
		return dto;
	}

	public static GeminiReqDto fromText(String text) {
		GeminiReqDto dto = new GeminiReqDto();
		dto.contents.add(Content.fromText(text));
		return dto;
	}

	@Getter
	@NoArgsConstructor
	public static class Content {
		private List<Part> parts = new ArrayList<>();

		public static Content fromText(String text) {
			Content c = new Content();
			c.parts.add(new Part(text));
			return c;
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
