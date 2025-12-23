package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.PostMediaType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post_media", indexes = {
    @Index(name = "idx_post_media_post", columnList = "post_id")
})
public class PostMedia extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_media_post"))
  private Post post;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PostMediaType mediaType;

  @Column(nullable = false, length = 600)
  private String url;
}
