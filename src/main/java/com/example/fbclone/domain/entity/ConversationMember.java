package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.ConversationMemberRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conversation_members",
    uniqueConstraints = @UniqueConstraint(name = "uk_conversation_member", columnNames = {"conversation_id", "user_id"}),
    indexes = {
        @Index(name = "idx_cm_user", columnList = "user_id"),
        @Index(name = "idx_cm_conversation", columnList = "conversation_id")
    }
)
public class ConversationMember extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cm_conversation"))
  private Conversation conversation;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cm_user"))
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ConversationMemberRole role = ConversationMemberRole.MEMBER;

  @Column(nullable = false)
  private Long lastReadMessageId = 0L;
}
