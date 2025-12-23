package com.example.fbclone.security;

import com.example.fbclone.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring("Bearer ".length()).trim();

    try {
      Jws<Claims> jws = jwtService.parse(token);
      Claims claims = jws.getBody();

      Long uid = ((Number) claims.get("uid")).longValue();
      String username = (String) claims.get("username");
      @SuppressWarnings("unchecked")
      List<String> roles = (List<String>) claims.get("roles");

      // strict: reject tokens for deleted/disabled/banned users
      var userOpt = userRepository.findById(uid);
      if (userOpt.isEmpty() || !userOpt.get().isEnabled() || userOpt.get().isBanned()) {
        SecurityContextHolder.clearContext();
        filterChain.doFilter(request, response);
        return;
      }

      var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
      var principal = new AuthUser(uid, username);
      var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(auth);
    } catch (Exception ex) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}
