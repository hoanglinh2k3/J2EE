package com.example.fbclone.dto.comment;

import com.example.fbclone.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class CommentResponse {
  private Long id;
  private Long postId;
  private UserResponse author;
  private String content;
  private Instant createdAt;
}
