package com.example.fbclone.controller;

import com.example.fbclone.dto.friend.FriendRequestResponse;
import com.example.fbclone.dto.friend.FriendshipResponse;
import com.example.fbclone.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  @PostMapping("/requests/{receiverId}")
  public FriendRequestResponse send(@PathVariable Long receiverId) {
    return friendService.sendRequest(receiverId);
  }

  @GetMapping("/requests/incoming")
  public List<FriendRequestResponse> incoming() {
    return friendService.incoming();
  }

  @GetMapping("/requests/outgoing")
  public List<FriendRequestResponse> outgoing() {
    return friendService.outgoing();
  }

  @PostMapping("/requests/{id}/accept")
  public FriendRequestResponse accept(@PathVariable Long id) {
    return friendService.accept(id);
  }

  @PostMapping("/requests/{id}/decline")
  public FriendRequestResponse decline(@PathVariable Long id) {
    return friendService.decline(id);
  }

  @PostMapping("/requests/{id}/cancel")
  public FriendRequestResponse cancel(@PathVariable Long id) {
    return friendService.cancel(id);
  }

  @GetMapping
  public List<FriendshipResponse> friends() {
    return friendService.listFriends();
  }

  @DeleteMapping("/{friendId}")
  public void unfriend(@PathVariable Long friendId) {
    friendService.unfriend(friendId);
  }
}
