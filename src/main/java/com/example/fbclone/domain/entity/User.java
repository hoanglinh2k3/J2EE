package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
    }
)
public class User extends BaseEntity {

  @Column(nullable = false, length = 40)
  private String username;

  @Column(nullable = false, length = 120)
  private String email;

  @Column(nullable = false, length = 120)
  private String passwordHash;

  @Column(nullable = false, length = 80)
  private String displayName;

  @Column(length = 400)
  private String bio;

  @Column(length = 400)
  private String avatarUrl;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(nullable = false)
  private boolean banned = false;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"),
      uniqueConstraints = @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_id"})
  )
  @Builder.Default
  private Set<Role> roles = new HashSet<>();
}
