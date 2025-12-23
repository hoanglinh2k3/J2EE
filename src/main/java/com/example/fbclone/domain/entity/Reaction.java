package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reactions",
    uniqueConstraints = @UniqueConstraint(name = "uk_reaction_post_user", columnNames = {"post_id", "user_id"}),
    indexes = {
        @Index(name = "idx_reaction_post", columnList = "post_id")
    }
)
public class Reaction extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reaction_post"))
  private Post post;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reaction_user"))
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ReactionType type;
}
