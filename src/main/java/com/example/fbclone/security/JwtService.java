package com.example.fbclone.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${app.jwt.issuer}")
  private String issuer;

  @Value("${app.jwt.secret-base64}")
  private String secretBase64;

  @Value("${app.jwt.access-token-minutes:120}")
  private long accessTokenMinutes;

  private SecretKey key() {
    byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(UserPrincipal principal) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(accessTokenMinutes * 60);

    List<String> roles = principal.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    Map<String, Object> claims = new HashMap<>();
    claims.put("uid", principal.getId());
    claims.put("roles", roles);
    claims.put("username", principal.getUsername());

    return Jwts.builder()
        .setIssuer(issuer)
        .setSubject(String.valueOf(principal.getId()))
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .addClaims(claims)
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key())
        .requireIssuer(issuer)
        .build()
        .parseClaimsJws(token);
  }
}
