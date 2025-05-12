package com.example.domain.aws.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.example.domain.user.entity.User
import com.example.domain.user.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Service
@RequiredArgsConstructor
class S3Service(
    private val amazonS3: AmazonS3,
    private val userRepository: UserRepository,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String
) {

    fun updateFile(multipartFile: MultipartFile, dirName: String, userId: Long): String {
        val fileName = dirName + "/" + UUID.randomUUID() + "_" + multipartFile.originalFilename
        val metadata = ObjectMetadata()
        metadata.contentLength = multipartFile.size
        metadata.contentType = multipartFile.contentType
        metadata.contentDisposition = "inline"


        try {
            multipartFile.inputStream.use { inputStream ->
                amazonS3.putObject(PutObjectRequest(bucket, fileName, inputStream, metadata))
            }
        } catch (e: IOException) {
            throw RuntimeException("S3 업로드에 실패하였습니다.", e)
        }

        userRepository.findById(userId).ifPresent { user: User ->
            val oldUrl = user.profileImage
            if (oldUrl != null && oldUrl.contains(bucket)) {
                val oldKey = oldUrl.substring(oldUrl.indexOf(bucket) + bucket.length + 1)
                amazonS3.deleteObject(bucket, oldKey)
            }

            val newUrl = amazonS3.getUrl(bucket, fileName).toString()
            user.profileImage = newUrl
            userRepository.save(user)
        }

        return amazonS3.getUrl(bucket, fileName).toString()
    }
}
