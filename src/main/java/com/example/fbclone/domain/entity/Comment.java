package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_comment_post_created", columnList = "post_id,createdAt")
})
public class Comment extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_post"))
  private Post post;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_author"))
  private User author;

  @Column(nullable = false, length = 2000)
  private String content;

  @Column(nullable = false)
  private boolean deleted = false;
}
