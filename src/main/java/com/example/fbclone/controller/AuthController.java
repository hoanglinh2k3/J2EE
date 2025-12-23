package com.example.fbclone.controller;

import com.example.fbclone.dto.auth.AuthResponse;
import com.example.fbclone.dto.auth.LoginRequest;
import com.example.fbclone.dto.auth.RegisterRequest;
import com.example.fbclone.dto.user.UserResponse;
import com.example.fbclone.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
    return authService.register(req);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest req) {
    return authService.login(req);
  }

  @GetMapping("/me")
  public UserResponse me() {
    return authService.me();
  }
}
