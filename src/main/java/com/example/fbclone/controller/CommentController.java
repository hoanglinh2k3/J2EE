package com.example.fbclone.controller;

import com.example.fbclone.dto.comment.CommentCreateRequest;
import com.example.fbclone.dto.comment.CommentResponse;
import com.example.fbclone.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/posts/{postId}/comments")
  @PreAuthorize("@perm.canViewPost(#postId) or hasRole('ADMIN')")
  public CommentResponse add(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequest req) {
    return commentService.addComment(postId, req);
  }

  @GetMapping("/posts/{postId}/comments")
  @PreAuthorize("@perm.canViewPost(#postId) or hasRole('ADMIN')")
  public Page<CommentResponse> list(@PathVariable Long postId, Pageable pageable) {
    return commentService.listComments(postId, pageable);
  }

  @DeleteMapping("/comments/{id}")
  @PreAuthorize("@perm.canDeleteComment(#id) or hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    commentService.deleteComment(id);
  }
}
