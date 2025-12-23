package com.example.fbclone.dto.chat;

import com.example.fbclone.domain.enums.MessageType;
import com.example.fbclone.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class MessageResponse {
  private Long id;
  private Long conversationId;
  private UserResponse sender;
  private MessageType type;
  private String content;
  private Instant createdAt;
}
