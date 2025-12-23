package com.example.fbclone.controller;

import com.example.fbclone.dto.user.UpdateMeRequest;
import com.example.fbclone.dto.user.UserResponse;
import com.example.fbclone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}")
  public UserResponse getProfile(@PathVariable Long id) {
    return userService.getProfile(id);
  }

  @PutMapping("/me")
  public UserResponse updateMe(@Valid @RequestBody UpdateMeRequest req) {
    return userService.updateMe(req);
  }

  @GetMapping("/search")
  public Page<UserResponse> search(@RequestParam(defaultValue = "") String q, Pageable pageable) {
    return userService.search(q, pageable);
  }
}
