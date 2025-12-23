package com.example.fbclone.dto.admin;

import com.example.fbclone.domain.enums.RoleName;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRolesRequest {
  @NotEmpty
  private Set<RoleName> roles;
}
