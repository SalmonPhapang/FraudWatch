
package com.fraudwatch.fraudruleengine.service;

import com.fraudwatch.fraudruleengine.dto.AuthResponse;
import com.fraudwatch.fraudruleengine.dto.LoginRequest;
import com.fraudwatch.fraudruleengine.entity.User;
import com.fraudwatch.fraudruleengine.repository.UserRepository;
import com.fraudwatch.fraudruleengine.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.expiration}")
    private Long expiration;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            return new AuthResponse(token, "Bearer", expiration / 1000); // Convert ms to seconds
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password", e);
        }
    }
}
