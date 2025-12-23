package com.example.fbclone.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
  private SecurityUtils() {}

  public static Long currentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) return null;
    if (auth.getPrincipal() instanceof AuthUser au) {
      return au.getId();
    }
    return null;
  }

  public static String currentUsername() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) return null;
    if (auth.getPrincipal() instanceof AuthUser au) {
      return au.getUsername();
    }
    return auth.getName();
  }

  public static boolean hasAuthority(String authority) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getAuthorities() == null) return false;
    for (GrantedAuthority ga : auth.getAuthorities()) {
      if (authority.equals(ga.getAuthority())) return true;
    }
    return false;
  }

  public static boolean isAdmin() {
    return hasAuthority("ROLE_ADMIN");
  }
}
