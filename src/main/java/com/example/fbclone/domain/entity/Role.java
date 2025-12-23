package com.example.fbclone.domain.entity;

import com.example.fbclone.domain.BaseEntity;
import com.example.fbclone.domain.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles", uniqueConstraints = {
    @UniqueConstraint(name = "uk_role_name", columnNames = "name")
})
public class Role extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private RoleName name;
}
