package com.example.fbclone.security;

import com.example.fbclone.domain.entity.Comment;
import com.example.fbclone.domain.entity.Post;
import com.example.fbclone.domain.enums.PostPrivacy;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.repository.CommentRepository;
import com.example.fbclone.repository.ConversationMemberRepository;
import com.example.fbclone.repository.FriendshipRepository;
import com.example.fbclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("perm")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PermissionService {

  private final PostRepository postRepository;
  private final FriendshipRepository friendshipRepository;
  private final CommentRepository commentRepository;
  private final ConversationMemberRepository conversationMemberRepository;

  public boolean isSelf(Long userId) {
    Long me = SecurityUtils.currentUserId();
    return me != null && me.equals(userId);
  }

  public boolean isPostOwner(Long postId) {
    Long me = SecurityUtils.currentUserId();
    if (me == null) return false;
    Post p = postRepository.findByIdAndDeletedFalse(postId)
        .orElseThrow(() -> new NotFoundException("Post not found"));
    return p.getAuthor().getId().equals(me);
  }

  public boolean canViewPost(Long postId) {
    Long me = SecurityUtils.currentUserId();
    if (me == null) return false;

    Post p = postRepository.findByIdAndDeletedFalse(postId)
        .orElseThrow(() -> new NotFoundException("Post not found"));

    if (p.getPrivacy() == PostPrivacy.PUBLIC) return true;
    if (p.getAuthor().getId().equals(me)) return true;

    if (p.getPrivacy() == PostPrivacy.FRIENDS) {
      return friendshipRepository.findBetween(me, p.getAuthor().getId()).isPresent();
    }

    // ONLY_ME
    return false;
  }

  public boolean canDeleteComment(Long commentId) {
    Long me = SecurityUtils.currentUserId();
    if (me == null) return false;
    Comment c = commentRepository.findByIdAndDeletedFalse(commentId)
        .orElseThrow(() -> new NotFoundException("Comment not found"));

    if (c.getAuthor().getId().equals(me)) return true;
    return c.getPost().getAuthor().getId().equals(me);
  }

  public boolean isConversationMember(Long conversationId) {
    Long me = SecurityUtils.currentUserId();
    if (me == null) return false;
    return conversationMemberRepository.isMember(conversationId, me);
  }

  public boolean isConversationOwner(Long conversationId) {
    Long me = SecurityUtils.currentUserId();
    if (me == null) return false;
    return conversationMemberRepository.hasRole(conversationId, me, com.example.fbclone.domain.enums.ConversationMemberRole.OWNER);
  }
}
