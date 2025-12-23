package com.example.fbclone.dto.friend;

import com.example.fbclone.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class FriendshipResponse {
  private Long id;
  private UserResponse friend;
  private Instant createdAt;
}
