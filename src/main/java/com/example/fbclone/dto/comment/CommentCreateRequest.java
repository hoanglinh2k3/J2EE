package com.example.fbclone.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequest {

  @NotBlank
  @Size(max = 2000)
  private String content;
}
