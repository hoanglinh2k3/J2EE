package com.example.fbclone.mapper;

import com.example.fbclone.domain.entity.User;
import com.example.fbclone.dto.user.UserResponse;

public final class UserMapper {
  private UserMapper() {}

  public static UserResponse toUserResponse(User u) {
    if (u == null) return null;
    return UserResponse.builder()
        .id(u.getId())
        .username(u.getUsername())
        .displayName(u.getDisplayName())
        .bio(u.getBio())
        .avatarUrl(u.getAvatarUrl())
        .build();
  }
}
