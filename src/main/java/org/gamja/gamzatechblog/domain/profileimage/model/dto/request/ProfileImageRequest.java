package org.gamja.gamzatechblog.domain.profileimage.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ProfileImageRequest(MultipartFile imageFile) {
}
