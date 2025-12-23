package com.example.fbclone.service;

import com.example.fbclone.domain.entity.Role;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.domain.enums.RoleName;
import com.example.fbclone.dto.admin.SetBanRequest;
import com.example.fbclone.dto.admin.UpdateUserRolesRequest;
import com.example.fbclone.dto.user.UpdateMeRequest;
import com.example.fbclone.dto.user.UserResponse;
import com.example.fbclone.exception.BadRequestException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.mapper.UserMapper;
import com.example.fbclone.repository.RoleRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UserResponse getProfile(Long userId) {
    User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    return UserMapper.toUserResponse(u);
  }

  @Transactional
  public UserResponse updateMe(UpdateMeRequest req) {
    Long uid = SecurityUtils.currentUserId();
    if (uid == null) throw new BadRequestException("Unauthenticated");

    User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));

    if (req.getDisplayName() != null) u.setDisplayName(req.getDisplayName());
    if (req.getBio() != null) u.setBio(req.getBio());
    if (req.getAvatarUrl() != null) u.setAvatarUrl(req.getAvatarUrl());

    return UserMapper.toUserResponse(userRepository.save(u));
  }

  public Page<UserResponse> search(String q, Pageable pageable) {
    if (q == null) q = "";
    return userRepository
        .findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(q, q, pageable)
        .map(UserMapper::toUserResponse);
  }

  public Page<UserResponse> adminListUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(UserMapper::toUserResponse);
  }

  @Transactional
  public UserResponse adminUpdateRoles(Long userId, UpdateUserRolesRequest req) {
    User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    Set<Role> roles = req.getRoles().stream()
        .map(rn -> roleRepository.findByName(rn).orElseThrow(() -> new NotFoundException("Role not found: " + rn)))
        .collect(Collectors.toSet());

    // always ensure ROLE_USER exists for normal app behavior
    Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();
    roles.add(userRole);

    u.setRoles(roles);
    return UserMapper.toUserResponse(userRepository.save(u));
  }

  @Transactional
  public UserResponse adminSetBan(Long userId, SetBanRequest req) {
    User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    u.setBanned(req.isBanned());
    return UserMapper.toUserResponse(userRepository.save(u));
  }
}
