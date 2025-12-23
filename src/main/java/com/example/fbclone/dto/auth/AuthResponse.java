package com.example.fbclone.dto.auth;

import com.example.fbclone.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
  private String accessToken;
  private String tokenType;
  private UserResponse user;
}
