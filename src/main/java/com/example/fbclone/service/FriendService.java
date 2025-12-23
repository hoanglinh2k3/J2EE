package com.example.fbclone.service;

import com.example.fbclone.domain.entity.FriendRequest;
import com.example.fbclone.domain.entity.Friendship;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.domain.enums.FriendRequestStatus;
import com.example.fbclone.dto.friend.FriendRequestResponse;
import com.example.fbclone.dto.friend.FriendshipResponse;
import com.example.fbclone.exception.BadRequestException;
import com.example.fbclone.exception.ForbiddenException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.mapper.FriendMapper;
import com.example.fbclone.repository.FriendRequestRepository;
import com.example.fbclone.repository.FriendshipRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRequestRepository friendRequestRepository;
  private final FriendshipRepository friendshipRepository;
  private final UserRepository userRepository;

  @Transactional
  public FriendRequestResponse sendRequest(Long receiverId) {
    Long me = SecurityUtils.currentUserId();
    if (me == null) throw new BadRequestException("Unauthenticated");
    if (me.equals(receiverId)) throw new BadRequestException("Cannot friend yourself");

    User sender = userRepository.findById(me).orElseThrow(() -> new NotFoundException("User not found"));
    User receiver = userRepository.findById(receiverId).orElseThrow(() -> new NotFoundException("Receiver not found"));

    if (friendshipRepository.findBetween(me, receiverId).isPresent()) {
      throw new BadRequestException("Already friends");
    }

    if (friendRequestRepository.findPendingBetween(me, receiverId).isPresent()) {
      throw new BadRequestException("A pending friend request already exists between you");
    }

    FriendRequest fr = FriendRequest.builder()
        .sender(sender)
        .receiver(receiver)
        .status(FriendRequestStatus.PENDING)
        .build();

    return FriendMapper.toResponse(friendRequestRepository.save(fr));
  }

  @Transactional(readOnly = true)
  public List<FriendRequestResponse> incoming() {
    Long me = SecurityUtils.currentUserId();
    return friendRequestRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(me, FriendRequestStatus.PENDING)
        .stream().map(FriendMapper::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public List<FriendRequestResponse> outgoing() {
    Long me = SecurityUtils.currentUserId();
    return friendRequestRepository.findBySenderIdAndStatusOrderByCreatedAtDesc(me, FriendRequestStatus.PENDING)
        .stream().map(FriendMapper::toResponse).toList();
  }

  @Transactional
  public FriendRequestResponse accept(Long requestId) {
    Long me = SecurityUtils.currentUserId();
    FriendRequest fr = friendRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Friend request not found"));

    if (!fr.getReceiver().getId().equals(me)) {
      throw new ForbiddenException("You cannot accept this request");
    }
    if (fr.getStatus() != FriendRequestStatus.PENDING) {
      throw new BadRequestException("Request is not pending");
    }

    fr.setStatus(FriendRequestStatus.ACCEPTED);
    friendRequestRepository.save(fr);

    // Create friendship (order the pair to enforce unique constraint user1<user2)
    Long a = fr.getSender().getId();
    Long b = fr.getReceiver().getId();
    if (friendshipRepository.findBetween(a, b).isEmpty()) {
      User u1 = fr.getSender();
      User u2 = fr.getReceiver();
      Friendship f = Friendship.builder()
          .user1(a < b ? u1 : u2)
          .user2(a < b ? u2 : u1)
          .build();
      friendshipRepository.save(f);
    }

    return FriendMapper.toResponse(fr);
  }

  @Transactional
  public FriendRequestResponse decline(Long requestId) {
    Long me = SecurityUtils.currentUserId();
    FriendRequest fr = friendRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Friend request not found"));
    if (!fr.getReceiver().getId().equals(me)) {
      throw new ForbiddenException("You cannot decline this request");
    }
    if (fr.getStatus() != FriendRequestStatus.PENDING) {
      throw new BadRequestException("Request is not pending");
    }
    fr.setStatus(FriendRequestStatus.DECLINED);
    return FriendMapper.toResponse(friendRequestRepository.save(fr));
  }

  @Transactional
  public FriendRequestResponse cancel(Long requestId) {
    Long me = SecurityUtils.currentUserId();
    FriendRequest fr = friendRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Friend request not found"));
    if (!fr.getSender().getId().equals(me)) {
      throw new ForbiddenException("You cannot cancel this request");
    }
    if (fr.getStatus() != FriendRequestStatus.PENDING) {
      throw new BadRequestException("Request is not pending");
    }
    fr.setStatus(FriendRequestStatus.CANCELED);
    return FriendMapper.toResponse(friendRequestRepository.save(fr));
  }

  @Transactional(readOnly = true)
  public List<FriendshipResponse> listFriends() {
    Long me = SecurityUtils.currentUserId();
    List<Friendship> friendships = friendshipRepository.findByUser1IdOrUser2Id(me, me);

    List<FriendshipResponse> result = new ArrayList<>();
    for (Friendship f : friendships) {
      User friend = f.getUser1().getId().equals(me) ? f.getUser2() : f.getUser1();
      result.add(FriendMapper.toResponse(f, friend));
    }

    result.sort(Comparator.comparing(FriendshipResponse::getCreatedAt).reversed());
    return result;
  }

  @Transactional
  public void unfriend(Long friendId) {
    Long me = SecurityUtils.currentUserId();
    Friendship f = friendshipRepository.findBetween(me, friendId)
        .orElseThrow(() -> new NotFoundException("Friendship not found"));
    friendshipRepository.delete(f);
  }
}
