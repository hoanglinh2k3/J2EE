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
@Table(name = "friendships",
    uniqueConstraints = @UniqueConstraint(name = "uk_friendship_pair", columnNames = {"user1_id", "user2_id"}),
    indexes = {
        @Index(name = "idx_friendship_user1", columnList = "user1_id"),
        @Index(name = "idx_friendship_user2", columnList = "user2_id")
    }
)
public class Friendship extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user1_id", nullable = false, foreignKey = @ForeignKey(name = "fk_friendship_user1"))
  private User user1;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user2_id", nullable = false, foreignKey = @ForeignKey(name = "fk_friendship_user2"))
  private User user2;
}
