package com.example.fbclone.dto.post;

import com.example.fbclone.domain.enums.PostPrivacy;
import com.example.fbclone.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PostResponse {
  private Long id;
  private UserResponse author;
  private String content;
  private PostPrivacy privacy;
  private Instant createdAt;
  private Instant updatedAt;
  private List<MediaResponse> media;
  private long reactionCount;
  private String myReaction; // null if none
}
