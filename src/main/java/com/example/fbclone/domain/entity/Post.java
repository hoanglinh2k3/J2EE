package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.PostPrivacy;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_post_author_created", columnList = "author_id,createdAt")
})
public class Post extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_author"))
  private User author;

  @Column(nullable = false, length = 5000)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PostPrivacy privacy = PostPrivacy.PUBLIC;

  @Column(nullable = false)
  private boolean deleted = false;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PostMedia> media = new ArrayList<>();
}
