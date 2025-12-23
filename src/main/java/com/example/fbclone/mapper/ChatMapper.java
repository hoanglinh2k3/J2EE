package com.example.fbclone.mapper;

import com.example.fbclone.domain.entity.Conversation;
import com.example.fbclone.domain.entity.Message;
import com.example.fbclone.dto.chat.ConversationResponse;
import com.example.fbclone.dto.chat.MessageResponse;

import java.util.List;

public final class ChatMapper {
  private ChatMapper() {}

  public static ConversationResponse toConversationResponse(Conversation c, List<com.example.fbclone.domain.entity.User> members) {
    return ConversationResponse.builder()
        .id(c.getId())
        .type(c.getType())
        .title(c.getTitle())
        .createdAt(c.getCreatedAt())
        .updatedAt(c.getUpdatedAt())
        .members(members.stream().map(UserMapper::toUserResponse).toList())
        .build();
  }

  public static MessageResponse toMessageResponse(Message m) {
    return MessageResponse.builder()
        .id(m.getId())
        .conversationId(m.getConversation().getId())
        .sender(UserMapper.toUserResponse(m.getSender()))
        .type(m.getType())
        .content(m.getContent())
        .createdAt(m.getCreatedAt())
        .build();
  }
}
