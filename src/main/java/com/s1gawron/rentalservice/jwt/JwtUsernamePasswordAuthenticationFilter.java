package com.s1gawron.rentalservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String USER_LOGIN_ENDPOINT = "/api/public/user/login";

    private final AuthenticationManager authenticationManager;

    private final JwtConfig jwtConfig;

    private final ObjectMapper objectMapper;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, final ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl(USER_LOGIN_ENDPOINT);
        this.jwtConfig = jwtConfig;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final UserLoginDTO userLoginDTO;

        try {
            userLoginDTO = objectMapper.readValue(request.getInputStream(), UserLoginDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Authentication authentication = new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password());
        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        final String jwtToken = Jwts.builder()
            .setSubject(authResult.getName())
            .claim("authorities", authResult.getAuthorities())
            .setIssuedAt(new Date())
            .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationInDays())))
            .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes()))
            .compact();

        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
