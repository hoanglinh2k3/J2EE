package com.example.fbclone.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
  private Long id;
  private String username;
}
