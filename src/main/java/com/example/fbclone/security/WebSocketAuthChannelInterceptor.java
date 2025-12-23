package com.example.fbclone.security;

import com.example.fbclone.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = resolveToken(accessor);
      if (StringUtils.hasText(token)) {
        try {
          Jws<Claims> jws = jwtService.parse(token);
          Claims claims = jws.getBody();

          Long uid = ((Number) claims.get("uid")).longValue();
          String username = (String) claims.get("username");
          @SuppressWarnings("unchecked")
          List<String> roles = (List<String>) claims.get("roles");

          var userOpt = userRepository.findById(uid);
          if (userOpt.isEmpty() || !userOpt.get().isEnabled() || userOpt.get().isBanned()) {
            return message;
          }

          var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
          var principal = new AuthUser(uid, username);
          Principal auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
          accessor.setUser(auth);
        } catch (Exception ex) {
          // ignore -> unauthenticated websocket
        }
      }
    }

    return message;
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    String auth = firstHeader(accessor, "Authorization");
    if (!StringUtils.hasText(auth)) auth = firstHeader(accessor, "authorization");
    if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
      return auth.substring("Bearer ".length()).trim();
    }
    String token = firstHeader(accessor, "token");
    if (StringUtils.hasText(token)) return token.trim();
    return null;
  }

  private String firstHeader(StompHeaderAccessor accessor, String name) {
    List<String> values = accessor.getNativeHeader(name);
    return (values == null || values.isEmpty()) ? null : values.get(0);
  }
}
