package com.example.fbclone.dto.post;

import com.example.fbclone.domain.enums.PostPrivacy;
import com.example.fbclone.domain.enums.PostMediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostCreateRequest {

  @NotBlank
  @Size(max = 5000)
  private String content;

  private PostPrivacy privacy = PostPrivacy.PUBLIC;

  // optional: media URLs (handled by client upload separately)
  private List<MediaItem> media;

  @Data
  public static class MediaItem {
    private PostMediaType mediaType;
    private String url;
  }
}
