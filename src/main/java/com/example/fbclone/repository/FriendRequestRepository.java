package com.example.fbclone.repository;

import com.example.fbclone.domain.entity.FriendRequest;
import com.example.fbclone.domain.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

  List<FriendRequest> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, FriendRequestStatus status);

  List<FriendRequest> findBySenderIdAndStatusOrderByCreatedAtDesc(Long senderId, FriendRequestStatus status);

  @Query("select fr from FriendRequest fr " +
      "where fr.status = com.example.fbclone.domain.enums.FriendRequestStatus.PENDING and (" +
      "  (fr.sender.id = :a and fr.receiver.id = :b) or (fr.sender.id = :b and fr.receiver.id = :a)" +
      ")")
  Optional<FriendRequest> findPendingBetween(Long a, Long b);
}
