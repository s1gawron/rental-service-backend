package com.s1gawron.rentalservice.initializer;

import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.repository.UserDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DefaultAdminUserInitializerTest {

    private static final String ADMIN_EMAIL = "admin@rental-service.com";

    @Autowired
    private UserDAO userDAO;

    @Test
    void shouldInitializeAdminUserOnApplicationStartup() {
        final Optional<User> result = userDAO.findByEmail(ADMIN_EMAIL);

        assertTrue(result.isPresent());
    }

}