package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "friend_requests", indexes = {
    @Index(name = "idx_fr_receiver_status", columnList = "receiver_id,status"),
    @Index(name = "idx_fr_sender_status", columnList = "sender_id,status")
})
public class FriendRequest extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fr_sender"))
  private User sender;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fr_receiver"))
  private User receiver;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private FriendRequestStatus status = FriendRequestStatus.PENDING;
}
