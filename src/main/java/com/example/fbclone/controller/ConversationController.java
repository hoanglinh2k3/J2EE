package com.example.fbclone.controller;

import com.example.fbclone.dto.chat.ConversationResponse;
import com.example.fbclone.dto.chat.CreateGroupConversationRequest;
import com.example.fbclone.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

  private final ConversationService conversationService;

  @PostMapping("/direct/{userId}")
  public ConversationResponse direct(@PathVariable Long userId) {
    return conversationService.createOrGetDirect(userId);
  }

  @PostMapping("/group")
  public ConversationResponse group(@Valid @RequestBody CreateGroupConversationRequest req) {
    return conversationService.createGroup(req);
  }

  @GetMapping
  public List<ConversationResponse> my() {
    return conversationService.myConversations();
  }

  @GetMapping("/{id}")
  @PreAuthorize("@perm.isConversationMember(#id) or hasRole('ADMIN')")
  public ConversationResponse get(@PathVariable Long id) {
    return conversationService.getConversation(id);
  }

  @PostMapping("/{conversationId}/members/{userId}")
  @PreAuthorize("@perm.isConversationOwner(#conversationId) or hasRole('ADMIN')")
  public ConversationResponse addMember(@PathVariable Long conversationId, @PathVariable Long userId) {
    return conversationService.addMember(conversationId, userId);
  }

  @DeleteMapping("/{conversationId}/members/{userId}")
  @PreAuthorize("@perm.isConversationOwner(#conversationId) or hasRole('ADMIN') or @perm.isSelf(#userId)")
  public ConversationResponse removeMember(@PathVariable Long conversationId, @PathVariable Long userId) {
    return conversationService.removeMember(conversationId, userId);
  }
}
