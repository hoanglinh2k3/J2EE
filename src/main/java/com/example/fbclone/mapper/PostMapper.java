package com.example.fbclone.mapper;

import com.example.fbclone.domain.entity.Post;
import com.example.fbclone.domain.entity.PostMedia;
import com.example.fbclone.dto.post.MediaResponse;
import com.example.fbclone.dto.post.PostResponse;

import java.util.List;

public final class PostMapper {
  private PostMapper() {}

  public static PostResponse toResponse(Post p, long reactionCount, String myReaction) {
    List<MediaResponse> media = p.getMedia() == null ? List.of() : p.getMedia().stream()
        .map(PostMapper::toMedia)
        .toList();
    return PostResponse.builder()
        .id(p.getId())
        .author(UserMapper.toUserResponse(p.getAuthor()))
        .content(p.getContent())
        .privacy(p.getPrivacy())
        .createdAt(p.getCreatedAt())
        .updatedAt(p.getUpdatedAt())
        .media(media)
        .reactionCount(reactionCount)
        .myReaction(myReaction)
        .build();
  }

  private static MediaResponse toMedia(PostMedia m) {
    return MediaResponse.builder()
        .id(m.getId())
        .mediaType(m.getMediaType())
        .url(m.getUrl())
        .build();
  }
}
