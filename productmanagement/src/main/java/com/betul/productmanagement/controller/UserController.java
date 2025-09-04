package com.betul.productmanagement.controller;

import com.betul.productmanagement.service.UserSessionService;
import com.betul.productmanagement.dto.AuthResponse;
import com.betul.productmanagement.dto.LoginRequest;
import com.betul.productmanagement.dto.RegisterRequest;
import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.entity.UserSession;
import com.betul.productmanagement.repository.UserRepository;
import com.betul.productmanagement.repository.UserSessionRepository;
import com.betul.productmanagement.security.JwtUtil;
import com.betul.productmanagement.service.UserService;
import com.betul.productmanagement.service.CustomUserDetailsService;
import com.betul.productmanagement.dto.RestResponseDTO;
import com.betul.productmanagement.dto.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserSessionRepository userSessionRepository;
    private final UserSessionService userSessionService;

    private final long jwtExpirationMs = 86400000; // örnek 1 gün

    //-----------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<RestResponseDTO<UserResponseDTO>> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        try {
            UserResponseDTO dto = userService.registerUser(request);
            RestResponseDTO<UserResponseDTO> response = new RestResponseDTO<>(
                    true,
                    "User registered successfully",
                    dto,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            RestResponseDTO<UserResponseDTO> response = new RestResponseDTO<>(
                    false,
                    e.getMessage(),
                    null,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //-------------------------------------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              @RequestHeader(value="X-Device-Id", required=false) String deviceId) {
        try {
            if (deviceId == null || deviceId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthResponse("Device ID missing"));
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            authenticationManager.authenticate(authToken);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Session kaydet
            userSessionService.createOrUpdateSession(user, deviceId, token);

            String message = user.getName() + ", welcome, login successful";
            AuthResponse authResponse = new AuthResponse(token, message, user.getName(), user.getEmail());

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse("Login failed: " + e.getMessage()));
        }
    }

    //-----------------------------------------------------------
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader,
                                         @RequestHeader("X-Device-Id") String deviceId) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // Servis üzerinden session sil
        userSessionService.deleteSessionByTokenAndDevice(token, deviceId);

        return ResponseEntity.ok("Logout successful");
    }
}


