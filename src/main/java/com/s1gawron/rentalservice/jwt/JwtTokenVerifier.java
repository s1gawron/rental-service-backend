package com.s1gawron.rentalservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.jwt.exception.UntrustedTokenException;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private static final String UNTRUSTED_TOKEN_MESSAGE = "Token cannot be trusted!";

    private final JwtConfig jwtConfig;

    private final ObjectMapper objectMapper;

    public JwtTokenVerifier(final JwtConfig jwtConfig, final ObjectMapper objectMapper) {
        this.jwtConfig = jwtConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
        throws ServletException, IOException {
        final String authorizationHeader = httpServletRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        try {
            final Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes()))
                .build()
                .parseClaimsJws(jwtConfig.getJwtTokenFromAuthorizationHeader(authorizationHeader))
                .getBody();

            final String email = claims.getSubject();
            final List<Map<String, String>> authorities = (List<Map<String, String>>) claims.get("authorities");
            final Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.get("authority")))
                .collect(Collectors.toSet());
            final Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, simpleGrantedAuthoritySet);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            final ErrorResponse errorResponse = new ErrorResponse(Instant.now().toString(), HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), UNTRUSTED_TOKEN_MESSAGE, httpServletRequest.getRequestURI());

            objectMapper.writeValue(httpServletResponse.getWriter(), errorResponse);

            throw UntrustedTokenException.create();
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
