package com.example.fbclone.service;

import com.example.fbclone.domain.entity.Conversation;
import com.example.fbclone.domain.entity.ConversationMember;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.domain.enums.ConversationMemberRole;
import com.example.fbclone.domain.enums.ConversationType;
import com.example.fbclone.dto.chat.ConversationResponse;
import com.example.fbclone.dto.chat.CreateGroupConversationRequest;
import com.example.fbclone.exception.BadRequestException;
import com.example.fbclone.exception.ForbiddenException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.mapper.ChatMapper;
import com.example.fbclone.repository.ConversationMemberRepository;
import com.example.fbclone.repository.ConversationRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConversationService {

  private final ConversationRepository conversationRepository;
  private final ConversationMemberRepository conversationMemberRepository;
  private final UserRepository userRepository;

  @Transactional
  public ConversationResponse createOrGetDirect(Long otherUserId) {
    Long me = SecurityUtils.currentUserId();
    if (me == null) throw new BadRequestException("Unauthenticated");
    if (me.equals(otherUserId)) throw new BadRequestException("Cannot create direct chat with yourself");

    User meUser = userRepository.findById(me).orElseThrow(() -> new NotFoundException("User not found"));
    User other = userRepository.findById(otherUserId).orElseThrow(() -> new NotFoundException("Other user not found"));

    Conversation existing = conversationRepository.findDirectBetweenUsers(me, otherUserId).orElse(null);
    if (existing != null) {
      return toResponse(existing);
    }

    Conversation c = Conversation.builder()
        .type(ConversationType.DIRECT)
        .title(null)
        .createdBy(meUser)
        .build();
    c = conversationRepository.save(c);

    ConversationMember cm1 = ConversationMember.builder()
        .conversation(c)
        .user(meUser)
        .role(ConversationMemberRole.MEMBER)
        .lastReadMessageId(0L)
        .build();
    ConversationMember cm2 = ConversationMember.builder()
        .conversation(c)
        .user(other)
        .role(ConversationMemberRole.MEMBER)
        .lastReadMessageId(0L)
        .build();

    conversationMemberRepository.save(cm1);
    conversationMemberRepository.save(cm2);

    return toResponse(c);
  }

  @Transactional
  public ConversationResponse createGroup(CreateGroupConversationRequest req) {
    Long me = SecurityUtils.currentUserId();
    User creator = userRepository.findById(me).orElseThrow(() -> new NotFoundException("User not found"));

    Conversation c = Conversation.builder()
        .type(ConversationType.GROUP)
        .title(req.getTitle())
        .createdBy(creator)
        .build();
    c = conversationRepository.save(c);

    Set<Long> memberIds = new HashSet<>(req.getMemberIds());
    memberIds.add(me);

    List<User> users = userRepository.findAllById(memberIds);

    // strict: make sure all ids exist
    if (users.size() != memberIds.size()) {
      throw new BadRequestException("Some memberIds do not exist");
    }

    for (User u : users) {
      ConversationMemberRole role = u.getId().equals(me) ? ConversationMemberRole.OWNER : ConversationMemberRole.MEMBER;
      ConversationMember cm = ConversationMember.builder()
          .conversation(c)
          .user(u)
          .role(role)
          .lastReadMessageId(0L)
          .build();
      conversationMemberRepository.save(cm);
    }

    return ChatMapper.toConversationResponse(c, users);
  }

  @Transactional(readOnly = true)
  public List<ConversationResponse> myConversations() {
    Long me = SecurityUtils.currentUserId();
    List<Conversation> list = conversationRepository.findAllForUser(me);
    List<ConversationResponse> result = new ArrayList<>();
    for (Conversation c : list) {
      result.add(toResponse(c));
    }
    return result;
  }

  @Transactional(readOnly = true)
  public ConversationResponse getConversation(Long conversationId) {
    if (!SecurityUtils.isAdmin() && !conversationMemberRepository.isMember(conversationId, SecurityUtils.currentUserId())) {
      throw new ForbiddenException("You are not a member of this conversation");
    }
    Conversation c = conversationRepository.findById(conversationId).orElseThrow(() -> new NotFoundException("Conversation not found"));
    return toResponse(c);
  }

  @Transactional
  public ConversationResponse addMember(Long conversationId, Long userId) {
    Conversation c = conversationRepository.findById(conversationId).orElseThrow(() -> new NotFoundException("Conversation not found"));
    if (c.getType() != ConversationType.GROUP) {
      throw new BadRequestException("Cannot add members to DIRECT conversation");
    }

    if (!SecurityUtils.isAdmin() && !conversationMemberRepository.hasRole(conversationId, SecurityUtils.currentUserId(), ConversationMemberRole.OWNER)) {
      throw new ForbiddenException("Only group OWNER can add members");
    }

    if (conversationMemberRepository.findByConversationIdAndUserId(conversationId, userId).isPresent()) {
      return toResponse(c); // already member
    }

    User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    ConversationMember cm = ConversationMember.builder()
        .conversation(c)
        .user(u)
        .role(ConversationMemberRole.MEMBER)
        .lastReadMessageId(0L)
        .build();
    conversationMemberRepository.save(cm);
    c.setUpdatedAt(java.time.Instant.now());
    conversationRepository.save(c);

    return toResponse(c);
  }

  @Transactional
  public ConversationResponse removeMember(Long conversationId, Long userId) {
    Conversation c = conversationRepository.findById(conversationId).orElseThrow(() -> new NotFoundException("Conversation not found"));

    Long me = SecurityUtils.currentUserId();
    boolean selfLeave = me != null && me.equals(userId);

    if (c.getType() == ConversationType.DIRECT) {
      throw new BadRequestException("DIRECT conversation members cannot be removed");
    }

    if (!selfLeave && !SecurityUtils.isAdmin() && !conversationMemberRepository.hasRole(conversationId, me, ConversationMemberRole.OWNER)) {
      throw new ForbiddenException("Only group OWNER can remove other members");
    }

    ConversationMember cm = conversationMemberRepository.findByConversationIdAndUserId(conversationId, userId)
        .orElseThrow(() -> new NotFoundException("Member not found"));

    if (cm.getRole() == ConversationMemberRole.OWNER && !SecurityUtils.isAdmin()) {
      throw new BadRequestException("OWNER cannot be removed");
    }

    conversationMemberRepository.delete(cm);
    c.setUpdatedAt(java.time.Instant.now());
    conversationRepository.save(c);

    return toResponse(c);
  }

  private ConversationResponse toResponse(Conversation c) {
    List<User> users = conversationMemberRepository.findByConversationId(c.getId()).stream().map(ConversationMember::getUser).toList();
    return ChatMapper.toConversationResponse(c, users);
  }
}
