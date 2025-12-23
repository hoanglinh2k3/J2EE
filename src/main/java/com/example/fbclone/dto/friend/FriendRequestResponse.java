package com.example.fbclone.dto.friend;

import com.example.fbclone.domain.enums.FriendRequestStatus;
import com.example.fbclone.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class FriendRequestResponse {
  private Long id;
  private UserResponse sender;
  private UserResponse receiver;
  private FriendRequestStatus status;
  private Instant createdAt;
}
