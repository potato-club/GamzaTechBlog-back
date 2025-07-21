package org.gamja.gamzatechblog.domain.profileimage.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileImageRequest {
	private MultipartFile imageFile;
}
