package org.example.expert.domain.aws.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;
	private final UserRepository userRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String updateFile(MultipartFile multipartFile, String dirName, Long userId) {
		String fileName = dirName + "/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());
		metadata.setContentDisposition("inline");


		try (InputStream inputStream = multipartFile.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
		} catch (IOException e) {
			throw new RuntimeException("S3 업로드에 실패하였습니다.", e);
		}

		userRepository.findById(userId).ifPresent(user -> {
			String oldUrl = user.getProfileImage();
			if (oldUrl != null && oldUrl.contains(bucket)) {
				String oldKey = oldUrl.substring(oldUrl.indexOf(bucket) + bucket.length() + 1);
				amazonS3.deleteObject(bucket, oldKey);
			}

			String newUrl = amazonS3.getUrl(bucket, fileName).toString();
			user.setProfileImage(newUrl);
			userRepository.save(user);
		});

		return amazonS3.getUrl(bucket, fileName).toString();
	}

}
