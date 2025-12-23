package com.example.fbclone.dto.chat;

import com.example.fbclone.domain.enums.ConversationType;
import com.example.fbclone.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ConversationResponse {
  private Long id;
  private ConversationType type;
  private String title;
  private Instant createdAt;
  private Instant updatedAt;
  private List<UserResponse> members;
}
