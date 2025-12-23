package com.example.fbclone.service;

import com.example.fbclone.domain.entity.Post;
import com.example.fbclone.domain.entity.PostMedia;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.domain.enums.PostPrivacy;
import com.example.fbclone.dto.post.PostCreateRequest;
import com.example.fbclone.dto.post.PostResponse;
import com.example.fbclone.dto.post.PostUpdateRequest;
import com.example.fbclone.exception.ForbiddenException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.mapper.PostMapper;
import com.example.fbclone.repository.FriendshipRepository;
import com.example.fbclone.repository.PostRepository;
import com.example.fbclone.repository.ReactionRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.PermissionService;
import com.example.fbclone.security.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final ReactionRepository reactionRepository;
  private final FriendshipRepository friendshipRepository;
  private final PermissionService permissionService;

  @Transactional
  public PostResponse create(PostCreateRequest req) {
    Long uid = SecurityUtils.currentUserId();
    User author = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));

    Post post = Post.builder()
        .author(author)
        .content(req.getContent())
        .privacy(req.getPrivacy() == null ? PostPrivacy.PUBLIC : req.getPrivacy())
        .deleted(false)
        .media(new ArrayList<>())
        .build();

    if (req.getMedia() != null) {
      for (var m : req.getMedia()) {
        if (m.getUrl() == null || m.getUrl().isBlank()) continue;
        PostMedia pm = PostMedia.builder()
            .post(post)
            .mediaType(m.getMediaType())
            .url(m.getUrl())
            .build();
        post.getMedia().add(pm);
      }
    }

    Post saved = postRepository.save(post);
    return PostMapper.toResponse(saved, 0L, null);
  }

  @Transactional(readOnly = true)
  public PostResponse getById(Long postId) {
    Post p = postRepository.findByIdAndDeletedFalse(postId)
        .orElseThrow(() -> new NotFoundException("Post not found"));

    if (!SecurityUtils.isAdmin()) {
      if (!permissionService.canViewPost(postId)) {
        throw new ForbiddenException("You are not allowed to view this post");
      }
    }

    long count = reactionRepository.countByPostId(postId);
    Long uid = SecurityUtils.currentUserId();
    String myReaction = uid == null ? null : reactionRepository.findByPostIdAndUserId(postId, uid)
        .map(r -> r.getType().name()).orElse(null);

    return PostMapper.toResponse(p, count, myReaction);
  }

  @Transactional(readOnly = true)
  public Page<PostResponse> feed(Pageable pageable) {
    Long uid = SecurityUtils.currentUserId();
    List<Long> friendIds = friendshipRepository.findFriendIds(uid);
    if (friendIds.isEmpty()) friendIds = List.of(-1L);

    return postRepository.findFeed(uid, friendIds, pageable)
        .map(p -> {
          long count = reactionRepository.countByPostId(p.getId());
          String myReaction = reactionRepository.findByPostIdAndUserId(p.getId(), uid)
              .map(r -> r.getType().name()).orElse(null);
          return PostMapper.toResponse(p, count, myReaction);
        });
  }

  @Transactional(readOnly = true)
  public Page<PostResponse> listUserPosts(Long userId, Pageable pageable) {
    Long me = SecurityUtils.currentUserId();

    if (SecurityUtils.isAdmin()) {
      return postRepository.findByAuthorIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
          .map(p -> PostMapper.toResponse(p, reactionRepository.countByPostId(p.getId()), null));
    }

    if (me != null && me.equals(userId)) {
      return postRepository.findByAuthorIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
          .map(p -> {
            String myReaction = reactionRepository.findByPostIdAndUserId(p.getId(), me).map(r -> r.getType().name()).orElse(null);
            return PostMapper.toResponse(p, reactionRepository.countByPostId(p.getId()), myReaction);
          });
    }

    boolean isFriend = me != null && friendshipRepository.findBetween(me, userId).isPresent();
    if (isFriend) {
      List<PostPrivacy> priv = List.of(PostPrivacy.PUBLIC, PostPrivacy.FRIENDS);
      return postRepository.findByAuthorIdAndDeletedFalseAndPrivacyInOrderByCreatedAtDesc(userId, priv, pageable)
          .map(p -> PostMapper.toResponse(p, reactionRepository.countByPostId(p.getId()), null));
    } else {
      return postRepository.findByAuthorIdAndDeletedFalseAndPrivacyOrderByCreatedAtDesc(userId, PostPrivacy.PUBLIC, pageable)
          .map(p -> PostMapper.toResponse(p, reactionRepository.countByPostId(p.getId()), null));
    }
  }

  @Transactional
  public PostResponse update(Long postId, PostUpdateRequest req) {
    Post p = postRepository.findByIdAndDeletedFalse(postId)
        .orElseThrow(() -> new NotFoundException("Post not found"));

    if (!SecurityUtils.isAdmin() && !p.getAuthor().getId().equals(SecurityUtils.currentUserId())) {
      throw new ForbiddenException("You are not allowed to edit this post");
    }

    p.setContent(req.getContent());
    p.setPrivacy(req.getPrivacy() == null ? PostPrivacy.PUBLIC : req.getPrivacy());

    Post saved = postRepository.save(p);

    long count = reactionRepository.countByPostId(postId);
    String myReaction = reactionRepository.findByPostIdAndUserId(postId, SecurityUtils.currentUserId())
        .map(r -> r.getType().name()).orElse(null);

    return PostMapper.toResponse(saved, count, myReaction);
  }

  @Transactional
  public void delete(Long postId) {
    Post p = postRepository.findByIdAndDeletedFalse(postId)
        .orElseThrow(() -> new NotFoundException("Post not found"));

    if (!SecurityUtils.isAdmin() && !p.getAuthor().getId().equals(SecurityUtils.currentUserId())) {
      throw new ForbiddenException("You are not allowed to delete this post");
    }

    p.setDeleted(true);
    postRepository.save(p);
  }
}
