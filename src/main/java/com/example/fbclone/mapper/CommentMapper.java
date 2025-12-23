package com.example.fbclone.mapper;

import com.example.fbclone.domain.entity.Comment;
import com.example.fbclone.dto.comment.CommentResponse;

public final class CommentMapper {
  private CommentMapper() {}

  public static CommentResponse toResponse(Comment c) {
    return CommentResponse.builder()
        .id(c.getId())
        .postId(c.getPost().getId())
        .author(UserMapper.toUserResponse(c.getAuthor()))
        .content(c.getContent())
        .createdAt(c.getCreatedAt())
        .build();
  }
}
