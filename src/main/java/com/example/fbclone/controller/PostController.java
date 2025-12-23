package com.example.fbclone.controller;

import com.example.fbclone.dto.post.PostCreateRequest;
import com.example.fbclone.dto.post.PostResponse;
import com.example.fbclone.dto.post.PostUpdateRequest;
import com.example.fbclone.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  @PostMapping("/posts")
  public PostResponse create(@Valid @RequestBody PostCreateRequest req) {
    return postService.create(req);
  }

  @GetMapping("/posts/{id}")
  @PreAuthorize("@perm.canViewPost(#id) or hasRole('ADMIN')")
  public PostResponse get(@PathVariable Long id) {
    return postService.getById(id);
  }

  @GetMapping("/feed")
  public Page<PostResponse> feed(Pageable pageable) {
    return postService.feed(pageable);
  }

  @GetMapping("/users/{userId}/posts")
  public Page<PostResponse> userPosts(@PathVariable Long userId, Pageable pageable) {
    return postService.listUserPosts(userId, pageable);
  }

  @PutMapping("/posts/{id}")
  @PreAuthorize("@perm.isPostOwner(#id) or hasRole('ADMIN')")
  public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostUpdateRequest req) {
    return postService.update(id, req);
  }

  @DeleteMapping("/posts/{id}")
  @PreAuthorize("@perm.isPostOwner(#id) or hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    postService.delete(id);
  }
}
