package com.example.fbclone.service;

import com.example.fbclone.domain.entity.Conversation;
import com.example.fbclone.domain.entity.Message;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.dto.chat.MessageResponse;
import com.example.fbclone.dto.chat.SendMessageRequest;
import com.example.fbclone.exception.ForbiddenException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.mapper.ChatMapper;
import com.example.fbclone.repository.ConversationMemberRepository;
import com.example.fbclone.repository.ConversationRepository;
import com.example.fbclone.repository.MessageRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final ConversationRepository conversationRepository;
  private final ConversationMemberRepository conversationMemberRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Transactional
  public MessageResponse send(Long conversationId, SendMessageRequest req) {
    Long me = SecurityUtils.currentUserId();
    if (!conversationMemberRepository.isMember(conversationId, me)) {
      throw new ForbiddenException("You are not a member of this conversation");
    }

    Conversation c = conversationRepository.findById(conversationId).orElseThrow(() -> new NotFoundException("Conversation not found"));
    User sender = userRepository.findById(me).orElseThrow(() -> new NotFoundException("User not found"));

    Message m = Message.builder()
        .conversation(c)
        .sender(sender)
        .type(req.getType())
        .content(req.getContent())
        .deleted(false)
        .build();
    m = messageRepository.save(m);

    // bump conversation updatedAt for ordering
    c.setUpdatedAt(Instant.now());
    conversationRepository.save(c);

    return ChatMapper.toMessageResponse(m);
  }

  @Transactional(readOnly = true)
  public Page<MessageResponse> list(Long conversationId, Pageable pageable) {
    Long me = SecurityUtils.currentUserId();
    if (!conversationMemberRepository.isMember(conversationId, me)) {
      throw new ForbiddenException("You are not a member of this conversation");
    }

    return messageRepository.findByConversationIdAndDeletedFalseOrderByCreatedAtDesc(conversationId, pageable)
        .map(ChatMapper::toMessageResponse);
  }
}
