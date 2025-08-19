package org.gamja.gamzatechblog.domain.admission.util;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public final class AdmissionNormalizer {

	private AdmissionNormalizer() {
	}

	public static String normalizeName(String raw) {
		if (raw == null)
			return null;
		return raw.trim().toLowerCase();
	}

	public static String normalizePhone(String raw) {
		if (raw == null)
			return null;
		if (!raw.matches("^01[016789]\\d{7,8}$")) {
			throw new BusinessException(ErrorCode.ADMISSION_INVALID_PHONE);
		}
		return raw;
	}
}
