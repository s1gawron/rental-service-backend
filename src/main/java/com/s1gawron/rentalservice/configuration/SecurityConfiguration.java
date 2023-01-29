package com.s1gawron.rentalservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.jwt.JwtConfig;
import com.s1gawron.rentalservice.jwt.JwtTokenVerifier;
import com.s1gawron.rentalservice.jwt.JwtUsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@CrossOrigin
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String USER_AUTH_QUERY = "SELECT email, password, active from user WHERE email=?";

    private static final String USER_AUTHORITY_QUERY = "SELECT email, user_role from user WHERE email=?";

    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/v2/api-docs",
        "/webjars/**"
    };

    private final DataSource dataSource;

    private final JwtConfig jwtConfig;

    private final ObjectMapper objectMapper;

    private final String frontendUrl;

    public SecurityConfiguration(final DataSource dataSource, final JwtConfig jwtConfig, final ObjectMapper objectMapper,
        @Value("${frontend.url}") final String frontendUrl) {
        this.dataSource = dataSource;
        this.jwtConfig = jwtConfig;
        this.objectMapper = objectMapper;
        this.frontendUrl = frontendUrl;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().configurationSource(request -> getCorsConfiguration())
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager(), jwtConfig, objectMapper))
            .addFilterAfter(new JwtTokenVerifier(jwtConfig, objectMapper), JwtUsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/api/public/**").permitAll()
            .antMatchers("/swagger-ui/**").permitAll()
            .anyRequest().authenticated();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(USER_AUTH_QUERY)
            .authoritiesByUsernameQuery(USER_AUTHORITY_QUERY)
            .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override public void configure(final WebSecurity web) {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }

    private CorsConfiguration getCorsConfiguration() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of(frontendUrl));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        return corsConfiguration;
    }
}