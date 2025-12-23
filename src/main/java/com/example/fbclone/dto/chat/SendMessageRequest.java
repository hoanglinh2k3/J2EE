package com.example.fbclone.dto.chat;

import com.example.fbclone.domain.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {

  private MessageType type = MessageType.TEXT;

  @NotBlank
  @Size(max = 5000)
  private String content;
}
