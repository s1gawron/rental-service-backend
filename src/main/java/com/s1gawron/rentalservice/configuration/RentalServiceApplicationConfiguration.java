package com.s1gawron.rentalservice.configuration;

import com.s1gawron.rentalservice.shared.exception.UserNotFoundException;
import com.s1gawron.rentalservice.user.repository.UserDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@Configuration
public class RentalServiceApplicationConfiguration {

    private final UserDAO userDAO;

    public RentalServiceApplicationConfiguration(final UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userDAO.findByEmail(username).orElseThrow(() -> UserNotFoundException.create(username));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
