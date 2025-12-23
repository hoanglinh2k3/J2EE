package com.example.fbclone.repository;

import com.example.fbclone.domain.entity.ConversationMember;
import com.example.fbclone.domain.enums.ConversationMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {

  Optional<ConversationMember> findByConversationIdAndUserId(Long conversationId, Long userId);

  List<ConversationMember> findByConversationId(Long conversationId);

  @Query("select count(cm) > 0 from ConversationMember cm where cm.conversation.id = :conversationId and cm.user.id = :userId")
  boolean isMember(Long conversationId, Long userId);

  @Query("select count(cm) > 0 from ConversationMember cm where cm.conversation.id = :conversationId and cm.user.id = :userId and cm.role = :role")
  boolean hasRole(Long conversationId, Long userId, ConversationMemberRole role);
}
