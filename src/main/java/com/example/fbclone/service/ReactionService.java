package com.example.fbclone.service;

import com.example.fbclone.domain.entity.Post;
import com.example.fbclone.domain.entity.Reaction;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.domain.enums.ReactionType;
import com.example.fbclone.exception.ForbiddenException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.repository.PostRepository;
import com.example.fbclone.repository.ReactionRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.PermissionService;
import com.example.fbclone.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionService {

  private final PostRepository postRepository;
  private final ReactionRepository reactionRepository;
  private final UserRepository userRepository;
  private final PermissionService permissionService;

  @Transactional
  public void setReaction(Long postId, ReactionType type) {
    Post post = postRepository.findByIdAndDeletedFalse(postId)
        .orElseThrow(() -> new NotFoundException("Post not found"));

    if (!SecurityUtils.isAdmin() && !permissionService.canViewPost(postId)) {
      throw new ForbiddenException("You are not allowed to react to this post");
    }

    Long uid = SecurityUtils.currentUserId();
    User user = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));

    Reaction r = reactionRepository.findByPostIdAndUserId(postId, uid)
        .orElseGet(() -> Reaction.builder().post(post).user(user).build());

    r.setType(type);
    reactionRepository.save(r);
  }

  @Transactional
  public void removeReaction(Long postId) {
    if (!SecurityUtils.isAdmin() && !permissionService.canViewPost(postId)) {
      throw new ForbiddenException("You are not allowed to remove reaction");
    }

    Long uid = SecurityUtils.currentUserId();
    reactionRepository.findByPostIdAndUserId(postId, uid).ifPresent(reactionRepository::delete);
  }
}
