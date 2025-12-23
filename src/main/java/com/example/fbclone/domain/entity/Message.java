package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_conversation_created", columnList = "conversation_id,createdAt")
})
public class Message extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_conversation"))
  private Conversation conversation;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_sender"))
  private User sender;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MessageType type = MessageType.TEXT;

  @Column(nullable = false, length = 5000)
  private String content;

  @Column(nullable = false)
  private boolean deleted = false;
}
