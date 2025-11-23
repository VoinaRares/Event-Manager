package com.example.event_manager.service;

import com.example.event_manager.dto.LoginResponseDTO;
import com.example.event_manager.model.User;
import com.example.event_manager.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public boolean existsById(Integer id) {
        return userRepository.existsById(id);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public LoginResponseDTO login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email is not registered"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateAccessToken(email, user.getId().longValue());
        String refreshToken = jwtUtil.generateRefreshToken(email, user.getId().longValue());

        return new LoginResponseDTO(
                user.getId().longValue(),
                user.getEmail(),
                accessToken,
                refreshToken);
    }

    public LoginResponseDTO refreshAccessToken(String refreshToken) {
        try {
            String email = jwtUtil.extractEmail(refreshToken);
            Long userId = jwtUtil.extractUserId(refreshToken);
            String tokenType = jwtUtil.extractTokenType(refreshToken);

            if (!"refresh".equals(tokenType)) {
                throw new RuntimeException("Invalid token type");
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                throw new RuntimeException("Refresh token expired");
            }
            String newAccessToken = jwtUtil.generateAccessToken(email, userId);
            String newRefreshToken = jwtUtil.generateRefreshToken(email, userId);

            return new LoginResponseDTO(userId, email, newAccessToken, newRefreshToken);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token has expired");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Malformed refresh token");
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid token signature");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token type must be 'refresh'");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }
}
