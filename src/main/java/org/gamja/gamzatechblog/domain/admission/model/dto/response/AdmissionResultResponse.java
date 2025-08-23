package org.gamja.gamzatechblog.domain.admission.model.dto.response;

import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;

public record AdmissionResultResponse(
	Long id,
	String name,
	String phoneDigits,
	AdmissionStatus status
) {
	@Override
	public String toString() {
		String masked = maskPhone(phoneDigits);
		return "AdmissionResultResponse[id=%s, name=%s, phoneDigits=%s, status=%s]"
			.formatted(id, name, masked, status);
	}

	private static String maskPhone(String digits) {
		if (digits == null || digits.isBlank())
			return "null";
		int len = digits.length();
		String tail = digits.substring(Math.max(0, len - 4));
		return "****" + tail;
	}
}
