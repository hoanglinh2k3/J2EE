package com.example.fbclone.service;

import com.example.fbclone.domain.entity.Comment;
import com.example.fbclone.domain.entity.Post;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.dto.comment.CommentCreateRequest;
import com.example.fbclone.dto.comment.CommentResponse;
import com.example.fbclone.exception.ForbiddenException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.mapper.CommentMapper;
import com.example.fbclone.repository.CommentRepository;
import com.example.fbclone.repository.PostRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.PermissionService;
import com.example.fbclone.security.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PermissionService permissionService;

  @Transactional
  public CommentResponse addComment(Long postId, CommentCreateRequest req) {
    Post post = postRepository.findByIdAndDeletedFalse(postId)
        .orElseThrow(() -> new NotFoundException("Post not found"));

    if (!SecurityUtils.isAdmin() && !permissionService.canViewPost(postId)) {
      throw new ForbiddenException("You are not allowed to comment on this post");
    }

    Long uid = SecurityUtils.currentUserId();
    User author = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));

    Comment c = Comment.builder()
        .post(post)
        .author(author)
        .content(req.getContent())
        .deleted(false)
        .build();

    return CommentMapper.toResponse(commentRepository.save(c));
  }

  @Transactional(readOnly = true)
  public Page<CommentResponse> listComments(Long postId, Pageable pageable) {
    if (!SecurityUtils.isAdmin() && !permissionService.canViewPost(postId)) {
      throw new ForbiddenException("You are not allowed to view comments");
    }

    return commentRepository.findByPostIdAndDeletedFalseOrderByCreatedAtAsc(postId, pageable)
        .map(CommentMapper::toResponse);
  }

  @Transactional
  public void deleteComment(Long commentId) {
    Comment c = commentRepository.findByIdAndDeletedFalse(commentId)
        .orElseThrow(() -> new NotFoundException("Comment not found"));

    if (!SecurityUtils.isAdmin() && !permissionService.canDeleteComment(commentId)) {
      throw new ForbiddenException("You are not allowed to delete this comment");
    }

    c.setDeleted(true);
    commentRepository.save(c);
  }
}
