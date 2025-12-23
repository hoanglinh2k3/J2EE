package com.example.fbclone.controller;

import com.example.fbclone.domain.enums.ReactionType;
import com.example.fbclone.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReactionController {

  private final ReactionService reactionService;

  @PutMapping("/posts/{postId}/reactions/{type}")
  @PreAuthorize("@perm.canViewPost(#postId) or hasRole('ADMIN')")
  public void react(@PathVariable Long postId, @PathVariable ReactionType type) {
    reactionService.setReaction(postId, type);
  }

  @DeleteMapping("/posts/{postId}/reactions")
  @PreAuthorize("@perm.canViewPost(#postId) or hasRole('ADMIN')")
  public void unreact(@PathVariable Long postId) {
    reactionService.removeReaction(postId);
  }
}
