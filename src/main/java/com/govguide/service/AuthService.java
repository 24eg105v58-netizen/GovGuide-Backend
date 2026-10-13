package com.govguide.service;

import com.govguide.config.JwtTokenProvider;
import com.govguide.dto.*;
import com.govguide.entity.User;
import com.govguide.exception.BadRequestException;
import com.govguide.exception.ResourceNotFoundException;
import com.govguide.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirmation password do not match");
        }

        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new BadRequestException("An account with this email address already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setMobileNumber(request.getMobileNumber().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        user.setPreferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "en");
        user.setAccountStatus("ACTIVE");

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole(), savedUser.getPreferredLanguage());
    }

    public AuthResponse loginUser(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
            throw new BadRequestException("Your account is currently inactive or suspended. Please contact support.");
        }

        if (!"ROLE_USER".equals(user.getRole())) {
            throw new BadCredentialsException("Invalid credentials for user login. If you are an administrator, please use the Admin Portal.");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getPreferredLanguage());
    }

    public AuthResponse loginAdmin(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid administrator credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid administrator credentials");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
            throw new BadRequestException("Administrator account is currently inactive.");
        }

        if (!"ROLE_ADMIN".equals(user.getRole())) {
            throw new BadCredentialsException("Access Denied: The specified account does not possess administrative privileges.");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getPreferredLanguage());
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getMobileNumber(), user.getRole(), user.getPreferredLanguage(), user.getAccountStatus(), user.getCreatedAt());
    }

    @Transactional
    public UserDto updateLanguage(String email, String language) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPreferredLanguage(language);
        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getMobileNumber(), saved.getRole(), saved.getPreferredLanguage(), saved.getAccountStatus(), saved.getCreatedAt());
    }
}