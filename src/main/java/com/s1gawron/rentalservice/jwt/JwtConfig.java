package com.s1gawron.rentalservice.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${application.jwt.secretKey}")
    private String secretKey;

    @Value("${application.jwt.tokenExpirationInDays}")
    private int tokenExpirationInDays;

    public String getJwtTokenFromAuthorizationHeader(final String tokenFromHeader) {
        return tokenFromHeader.replace("Bearer ", "");
    }

    public JwtConfig() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public int getTokenExpirationInDays() {
        return tokenExpirationInDays;
    }
}
