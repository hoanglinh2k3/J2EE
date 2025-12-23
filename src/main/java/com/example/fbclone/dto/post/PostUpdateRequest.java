package com.example.fbclone.dto.post;

import com.example.fbclone.domain.enums.PostPrivacy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostUpdateRequest {

  @NotBlank
  @Size(max = 5000)
  private String content;

  private PostPrivacy privacy = PostPrivacy.PUBLIC;
}
