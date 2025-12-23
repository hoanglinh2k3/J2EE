package com.example.fbclone.service;

import com.example.fbclone.domain.entity.Role;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.domain.enums.RoleName;
import com.example.fbclone.dto.auth.AuthResponse;
import com.example.fbclone.dto.auth.LoginRequest;
import com.example.fbclone.dto.auth.RegisterRequest;
import com.example.fbclone.dto.user.UserResponse;
import com.example.fbclone.exception.BadRequestException;
import com.example.fbclone.exception.NotFoundException;
import com.example.fbclone.mapper.UserMapper;
import com.example.fbclone.repository.RoleRepository;
import com.example.fbclone.repository.UserRepository;
import com.example.fbclone.security.JwtService;
import com.example.fbclone.security.SecurityUtils;
import com.example.fbclone.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Transactional
  public AuthResponse register(RegisterRequest req) {
    if (userRepository.existsByUsername(req.getUsername())) {
      throw new BadRequestException("Username already exists");
    }
    if (userRepository.existsByEmail(req.getEmail())) {
      throw new BadRequestException("Email already exists");
    }

    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
        .orElseThrow(() -> new NotFoundException("ROLE_USER not seeded"));

    User u = User.builder()
        .username(req.getUsername())
        .email(req.getEmail())
        .displayName(req.getDisplayName())
        .passwordHash(passwordEncoder.encode(req.getPassword()))
        .enabled(true)
        .banned(false)
        .roles(Set.of(userRole))
        .build();

    u = userRepository.save(u);

    // auto login after register
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
    String token = jwtService.generateAccessToken(principal);

    return new AuthResponse(token, "Bearer", UserMapper.toUserResponse(u));
  }

  public AuthResponse login(LoginRequest req) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUsernameOrEmail(), req.getPassword()));
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

    User u = userRepository.findById(principal.getId())
        .orElseThrow(() -> new NotFoundException("User not found"));

    String token = jwtService.generateAccessToken(principal);
    return new AuthResponse(token, "Bearer", UserMapper.toUserResponse(u));
  }

  public UserResponse me() {
    Long uid = SecurityUtils.currentUserId();
    if (uid == null) throw new NotFoundException("Unauthenticated");
    User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));
    return UserMapper.toUserResponse(u);
  }
}
