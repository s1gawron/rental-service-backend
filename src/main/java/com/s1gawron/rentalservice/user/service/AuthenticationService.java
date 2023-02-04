package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.configuration.jwt.JwtService;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginRequest;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    public AuthenticationService(final UserRepository userRepository, final AuthenticationManager authManager, final JwtService jwtService) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse loginUser(final UserLoginRequest userLoginRequest) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password()));

        final User user = userRepository.findByEmail(userLoginRequest.email())
            .orElseThrow(() -> UserNotFoundException.create(userLoginRequest.email()));
        final String token = jwtService.generateToken(Map.of(), user);

        return new AuthenticationResponse(token);
    }
}