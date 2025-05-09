package org.example.expert.domain.aws.controller;

import org.example.expert.domain.aws.service.S3Service;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

	private final S3Service s3Service;

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam("file") MultipartFile file
	) {
		Long userId = authUser.getId();
		String imageUrl = s3Service.updateFile(file, "profile/" + userId, userId);
		return ResponseEntity.ok(imageUrl);
	}
}
