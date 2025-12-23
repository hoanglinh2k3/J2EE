package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.ConversationType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conversations", indexes = {
    @Index(name = "idx_conversation_created", columnList = "createdAt")
})
public class Conversation extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ConversationType type;

  @Column(length = 120)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id", foreignKey = @ForeignKey(name = "fk_conversation_created_by"))
  private User createdBy;
}
