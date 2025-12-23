package com.example.fbclone.controller;

import com.example.fbclone.dto.admin.SetBanRequest;
import com.example.fbclone.dto.admin.UpdateUserRolesRequest;
import com.example.fbclone.dto.user.UserResponse;
import com.example.fbclone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserService userService;

  @GetMapping("/users")
  public Page<UserResponse> listUsers(Pageable pageable) {
    return userService.adminListUsers(pageable);
  }

  @PatchMapping("/users/{id}/roles")
  public UserResponse updateRoles(@PathVariable Long id, @Valid @RequestBody UpdateUserRolesRequest req) {
    return userService.adminUpdateRoles(id, req);
  }

  @PatchMapping("/users/{id}/ban")
  public UserResponse ban(@PathVariable Long id, @RequestBody SetBanRequest req) {
    return userService.adminSetBan(id, req);
  }
}
