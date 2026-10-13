package com.govguide;

import com.govguide.config.JwtTokenProvider;
import com.govguide.dto.AuthRequest;
import com.govguide.dto.AuthResponse;
import com.govguide.dto.RegisterRequest;
import com.govguide.entity.User;
import com.govguide.exception.BadRequestException;
import com.govguide.repository.UserRepository;
import com.govguide.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, tokenProvider);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Ramesh Kumar");
        req.setEmail("ramesh@example.com");
        req.setMobileNumber("9876543210");
        req.setPassword("Secret@123");
        req.setConfirmPassword("Secret@123");
        req.setPreferredLanguage("te");

        when(userRepository.existsByEmail("ramesh@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret@123")).thenReturn("encodedPassword");
        
        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setFullName("Ramesh Kumar");
        savedUser.setEmail("ramesh@example.com");
        savedUser.setRole("ROLE_USER");
        savedUser.setPreferredLanguage("te");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenProvider.generateToken(10L, "ramesh@example.com", "ROLE_USER")).thenReturn("mock-jwt-token");

        AuthResponse resp = authService.registerUser(req);

        assertNotNull(resp);
        assertEquals("mock-jwt-token", resp.getToken());
        assertEquals("ramesh@example.com", resp.getEmail());
        assertEquals("te", resp.getPreferredLanguage());
    }

    @Test
    void testRegisterPasswordMismatchThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@example.com");
        req.setPassword("Password123");
        req.setConfirmPassword("Mismatch123");

        assertThrows(BadRequestException.class, () -> authService.registerUser(req));
    }

    @Test
    void testLoginWithIncorrectPasswordThrowsException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword");
        user.setRole("ROLE_USER");
        user.setAccountStatus("ACTIVE");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass", "hashedPassword")).thenReturn(false);

        AuthRequest req = new AuthRequest("user@example.com", "WrongPass");
        assertThrows(BadCredentialsException.class, () -> authService.loginUser(req));
    }

    @Test
    void testAdminLoginRejectsNormalUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword");
        user.setRole("ROLE_USER"); // Normal user trying admin login
        user.setAccountStatus("ACTIVE");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("CorrectPass", "hashedPassword")).thenReturn(true);

        AuthRequest req = new AuthRequest("user@example.com", "CorrectPass");
        assertThrows(BadCredentialsException.class, () -> authService.loginAdmin(req));
    }
}
