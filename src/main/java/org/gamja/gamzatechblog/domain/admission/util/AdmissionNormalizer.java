package org.gamja.gamzatechblog.domain.admission.util;

import java.util.Locale;
import java.util.regex.Pattern;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;

public final class AdmissionNormalizer {

	public static final String PHONE_REGEX = "^01[016789]\\d{7,8}$";
	private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

	private AdmissionNormalizer() {
	}

	public static String normalizeName(String raw) {
		if (raw == null)
			return null;
		return raw.trim().toLowerCase(Locale.ROOT);
	}

	public static String normalizePhone(String raw) {
		if (raw == null)
			return null;
		if (!PHONE_PATTERN.matcher(raw).matches()) {
			throw new BusinessException(ErrorCode.ADMISSION_INVALID_PHONE);
		}
		return raw;
	}
}
