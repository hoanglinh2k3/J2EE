package com.example.fbclone.repository;

import com.example.fbclone.domain.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

  @Query("select f from Friendship f " +
      "where (f.user1.id = :a and f.user2.id = :b) or (f.user1.id = :b and f.user2.id = :a)")
  Optional<Friendship> findBetween(Long a, Long b);

  @Query("select case when f.user1.id = :userId then f.user2.id else f.user1.id end " +
      "from Friendship f where f.user1.id = :userId or f.user2.id = :userId")
  List<Long> findFriendIds(Long userId);

  java.util.List<Friendship> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);
}
