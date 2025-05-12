package com.example.domain.comment.dto.request

import jakarta.validation.constraints.NotBlank
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor

data class CommentSaveRequest(

    @field:NotBlank
    var contents: String
)
