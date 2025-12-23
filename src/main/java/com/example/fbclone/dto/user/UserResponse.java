package com.example.fbclone.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {
  private Long id;
  private String username;
  private String displayName;
  private String bio;
  private String avatarUrl;
}
