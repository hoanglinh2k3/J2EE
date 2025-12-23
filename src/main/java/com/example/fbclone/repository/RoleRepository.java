package com.example.fbclone.repository;

import com.example.fbclone.domain.entity.Role;
import com.example.fbclone.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(RoleName name);
}
