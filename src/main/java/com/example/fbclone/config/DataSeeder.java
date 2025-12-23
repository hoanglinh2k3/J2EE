package com.example.fbclone.config;

import com.example.fbclone.domain.entity.Role;
import com.example.fbclone.domain.entity.User;
import com.example.fbclone.domain.enums.RoleName;
import com.example.fbclone.repository.RoleRepository;
import com.example.fbclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.bootstrap-admin.enabled:false}")
  private boolean bootstrapAdminEnabled;

  @Value("${app.bootstrap-admin.username:admin}")
  private String adminUsername;

  @Value("${app.bootstrap-admin.email:admin@local}")
  private String adminEmail;

  @Value("${app.bootstrap-admin.password:Admin@123456}")
  private String adminPassword;

  @Override
  public void run(String... args) {
    ensureRole(RoleName.ROLE_USER);
    ensureRole(RoleName.ROLE_ADMIN);

    if (bootstrapAdminEnabled) {
      userRepository.findByUsername(adminUsername).orElseGet(() -> {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();

        User admin = User.builder()
            .username(adminUsername)
            .email(adminEmail)
            .displayName("Administrator")
            .passwordHash(passwordEncoder.encode(adminPassword))
            .enabled(true)
            .banned(false)
            .roles(Set.of(adminRole, userRole))
            .build();
        return userRepository.save(admin);
      });
    }
  }

  private void ensureRole(RoleName name) {
    roleRepository.findByName(name).orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
  }
}
