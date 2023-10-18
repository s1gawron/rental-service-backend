package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.security.JwtService;
import com.s1gawron.rentalservice.shared.exception.UserNotFoundException;
import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.repository.UserDAO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserAuthenticationService {

    private final UserDAO userDAO;

    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    public UserAuthenticationService(final UserDAO userDAO, final AuthenticationManager authManager, final JwtService jwtService) {
        this.userDAO = userDAO;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse loginUser(final UserLoginDTO userLoginDTO) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password()));

        final User user = userDAO.findByEmail(userLoginDTO.email()).orElseThrow(() -> UserNotFoundException.create(userLoginDTO.email()));
        final String token = jwtService.generateToken(Map.of(), user);

        return new AuthenticationResponse(token);
    }
}
