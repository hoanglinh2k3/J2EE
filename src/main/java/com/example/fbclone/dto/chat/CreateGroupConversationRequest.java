package com.example.fbclone.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupConversationRequest {

  @NotBlank
  @Size(min = 1, max = 120)
  private String title;

  @NotEmpty
  private List<Long> memberIds; // including yourself is optional; server will add
}
