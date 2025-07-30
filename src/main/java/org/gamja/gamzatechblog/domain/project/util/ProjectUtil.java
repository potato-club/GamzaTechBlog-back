package org.gamja.gamzatechblog.domain.project.util;

import org.springframework.stereotype.Component;

@Component
public class ProjectUtil {

	public String makeSnippet(String description, int maxLength) {
		if (description == null || description.length() <= maxLength) {
			return description;
		}
		return description.substring(0, maxLength) + "…";
	}

	public String formatDuration(String duration) {
		return duration;
	}
}
