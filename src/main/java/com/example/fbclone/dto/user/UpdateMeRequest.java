package com.example.fbclone.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMeRequest {

  @Size(min = 2, max = 80)
  private String displayName;

  @Size(max = 400)
  private String bio;

  @Size(max = 400)
  private String avatarUrl;
}
