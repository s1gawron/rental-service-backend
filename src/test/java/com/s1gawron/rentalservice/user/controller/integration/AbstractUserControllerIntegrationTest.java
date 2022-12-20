package com.s1gawron.rentalservice.user.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractUserControllerIntegrationTest {

    private static final String USER_LOGIN_ENDPOINT = "/api/public/user/login";

    protected static final String EMAIL = "test@test.pl";

    protected static final String PASSWORD = "Start00!";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    private UserRepository userRepository;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, PASSWORD, "John", "Kowalski", "CUSTOMER", addressDTO);
        userService.validateAndRegisterUser(userRegisterDTO);
    }

    @AfterEach
    @Transactional
    void cleanUp() {
        userRepository.deleteAll();
    }

    @SneakyThrows
    protected MvcResult performLoginAction(final UserLoginDTO userLoginDTO) {
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson).contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(request).andReturn();
    }

}
