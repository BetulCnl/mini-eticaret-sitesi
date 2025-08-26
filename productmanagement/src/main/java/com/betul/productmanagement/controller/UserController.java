package com.betul.productmanagement.controller;

import com.betul.productmanagement.dto.AuthResponse;
import com.betul.productmanagement.dto.LoginRequest;
import com.betul.productmanagement.dto.RegisterRequest;
import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.repository.UserRepository;
import com.betul.productmanagement.security.JwtUtil;
import com.betul.productmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.betul.productmanagement.dto.RestResponseDTO;
import com.betul.productmanagement.dto.UserResponseDTO;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager; //email şifre eşleşmesi kontrolü
    private final JwtUtil jwtUtil; //token oluşturma doğrulama
    private final UserRepository userRepository;

    //-----------------------------------------------------------
    //bu alttaki kısımda önceden saçma olarak user nesnesi controller manuel oluşturuyordu, şimdi RegisterRequest alıyor
    @PostMapping("/register")
    public ResponseEntity<RestResponseDTO<UserResponseDTO>> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        try {
            // Servis artık UserResponseDTO döndürüyor
            UserResponseDTO dto = userService.registerUser(request);

            // RestResponse içine sarıyoruz ve tarih ekliyoruz
            RestResponseDTO<UserResponseDTO> response = new RestResponseDTO<>(
                    true,
                    "User registered successfully",
                    dto,
                    LocalDateTime.now()  // Tarih burada ekleniyor
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            authenticationManager.authenticate(authToken);

            String token = jwtUtil.generateToken(request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String message = user.getName() + ", welcome, login successful";

            AuthResponse authResponse = new AuthResponse(token, message, user.getName(), user.getEmail());
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid email or password"));
        }
    }
}
