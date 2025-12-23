package com.example.fbclone.controller;

import com.example.fbclone.dto.chat.MessageResponse;
import com.example.fbclone.dto.chat.SendMessageRequest;
import com.example.fbclone.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @GetMapping
  @PreAuthorize("@perm.isConversationMember(#conversationId) or hasRole('ADMIN')")
  public Page<MessageResponse> list(@PathVariable Long conversationId, Pageable pageable) {
    return messageService.list(conversationId, pageable);
  }

  @PostMapping
  @PreAuthorize("@perm.isConversationMember(#conversationId)")
  public MessageResponse send(@PathVariable Long conversationId, @Valid @RequestBody SendMessageRequest req) {
    return messageService.send(conversationId, req);
  }
}
