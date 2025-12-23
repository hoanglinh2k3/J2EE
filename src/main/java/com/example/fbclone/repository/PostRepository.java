package com.example.fbclone.repository;

import com.example.fbclone.domain.entity.Post;
import com.example.fbclone.domain.enums.PostPrivacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndDeletedFalse(Long id);

  Page<Post> findByAuthorIdAndDeletedFalseOrderByCreatedAtDesc(Long authorId, Pageable pageable);

  Page<Post> findByAuthorIdAndDeletedFalseAndPrivacyInOrderByCreatedAtDesc(Long authorId, List<PostPrivacy> privacies, Pageable pageable);

  Page<Post> findByAuthorIdAndDeletedFalseAndPrivacyOrderByCreatedAtDesc(Long authorId, PostPrivacy privacy, Pageable pageable);

  @Query("select p from Post p " +
      "where p.deleted = false and (" +
      "  p.privacy = com.example.fbclone.domain.enums.PostPrivacy.PUBLIC " +
      "  or p.author.id = :userId " +
      "  or (p.privacy = com.example.fbclone.domain.enums.PostPrivacy.FRIENDS and p.author.id in :friendIds)" +
      ") order by p.createdAt desc")
  Page<Post> findFeed(Long userId, List<Long> friendIds, Pageable pageable);
}
