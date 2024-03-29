package com.s1gawron.rentalservice.initializer;

import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminUserInitializer implements CommandLineRunner {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final String adminEmail;

    private final String adminPassword;

    public DefaultAdminUserInitializer(final UserService userService, final PasswordEncoder passwordEncoder,
        @Value("${application.admin.email}") final String adminEmail, @Value("${application.admin.password}") final String adminPassword) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(final String... args) {
        if (userService.getUserByEmail(adminEmail).isEmpty()) {
            final String encryptedPassword = passwordEncoder.encode(adminPassword);
            final User adminUser = new User(true, adminEmail, encryptedPassword, "admin", "admin", UserRole.ADMIN, null);

            userService.saveUser(adminUser);
        }
    }
}
