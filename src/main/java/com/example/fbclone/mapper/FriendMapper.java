package com.example.fbclone.mapper;

import com.example.fbclone.domain.entity.FriendRequest;
import com.example.fbclone.domain.entity.Friendship;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.dto.friend.FriendRequestResponse;
import com.example.fbclone.dto.friend.FriendshipResponse;

public final class FriendMapper {
  private FriendMapper() {}

  public static FriendRequestResponse toResponse(FriendRequest fr) {
    return FriendRequestResponse.builder()
        .id(fr.getId())
        .sender(UserMapper.toUserResponse(fr.getSender()))
        .receiver(UserMapper.toUserResponse(fr.getReceiver()))
        .status(fr.getStatus())
        .createdAt(fr.getCreatedAt())
        .build();
  }

  public static FriendshipResponse toResponse(Friendship f, User friend) {
    return FriendshipResponse.builder()
        .id(f.getId())
        .friend(UserMapper.toUserResponse(friend))
        .createdAt(f.getCreatedAt())
        .build();
  }
}
