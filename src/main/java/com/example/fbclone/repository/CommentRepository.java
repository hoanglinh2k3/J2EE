package com.example.fbclone.repository;

import com.example.fbclone.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  Page<Comment> findByPostIdAndDeletedFalseOrderByCreatedAtAsc(Long postId, Pageable pageable);
  Optional<Comment> findByIdAndDeletedFalse(Long id);
}
