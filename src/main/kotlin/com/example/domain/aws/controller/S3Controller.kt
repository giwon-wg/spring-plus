package com.example.domain.aws.controller

import com.example.domain.common.dto.AuthUser
import lombok.RequiredArgsConstructor
import com.example.domain.aws.service.S3Service
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
class S3Controller(
	private val s3Service: S3Service
) {

    @PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        val userId = authUser.id
        val imageUrl = s3Service.updateFile(file, "profile/$userId", userId)
        return ResponseEntity.ok(imageUrl)
    }
}
