package com.example.fbclone.dto.post;

import com.example.fbclone.domain.enums.PostMediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MediaResponse {
  private Long id;
  private PostMediaType mediaType;
  private String url;
}
