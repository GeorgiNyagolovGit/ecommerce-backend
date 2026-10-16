package com.shop.ecommerce.auth.service;

import com.shop.ecommerce.auth.dto.AuthResponse;
import com.shop.ecommerce.auth.dto.LoginRequest;
import com.shop.ecommerce.auth.dto.RegisterRequest;
import com.shop.ecommerce.auth.entity.Role;
import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.RoleRepository;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.auth.security.CustomUserDetailsService;
import com.shop.ecommerce.auth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() ->
                        new IllegalStateException("Customer role not found"));

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(customerRole));

        User savedUser = userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(savedUser.getEmail());

        String token = jwtService.generateToken(userDetails.getUsername());

        return toAuthResponse(savedUser, token);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getPassword()
                        )
                );

        User user = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("User not found"));

        String token = jwtService.generateToken(user.getEmail());

        return toAuthResponse(user, token);
    }

    private AuthResponse toAuthResponse(User user, String token) {
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                token,
                roles
        );
    }
}