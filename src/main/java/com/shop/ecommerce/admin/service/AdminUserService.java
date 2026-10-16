package com.shop.ecommerce.admin.service;

import com.shop.ecommerce.admin.dto.UserAdminDto;
import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserAdminDto> getUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public UserAdminDto getUser(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserAdminDto toDto(User user) {
        return new UserAdminDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }
}
