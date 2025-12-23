package com.example.fbclone.repository;

import com.example.fbclone.domain.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

  @Query("select c from Conversation c " +
      "where c.type = com.example.fbclone.domain.enums.ConversationType.DIRECT " +
      "and c.id in (select cm1.conversation.id from com.example.fbclone.domain.entity.ConversationMember cm1 where cm1.user.id = :userA) " +
      "and c.id in (select cm2.conversation.id from com.example.fbclone.domain.entity.ConversationMember cm2 where cm2.user.id = :userB)")
  Optional<Conversation> findDirectBetweenUsers(Long userA, Long userB);

  @Query("select c from Conversation c " +
      "where c.id in (select cm.conversation.id from com.example.fbclone.domain.entity.ConversationMember cm where cm.user.id = :userId) " +
      "order by c.updatedAt desc")
  List<Conversation> findAllForUser(Long userId);
}
