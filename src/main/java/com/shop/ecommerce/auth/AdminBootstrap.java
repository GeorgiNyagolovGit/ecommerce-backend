package com.shop.ecommerce.auth;

import com.shop.ecommerce.auth.entity.Role;
import com.shop.ecommerce.auth.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public AdminBootstrap(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        createRoleIfMissing("ROLE_CUSTOMER");
        createRoleIfMissing("ROLE_ADMIN");
    }

    private void createRoleIfMissing(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(new Role(name));
        }
    }
}