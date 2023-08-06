package com.s1gawron.rentalservice.user.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractUserControllerIntegrationTest {

    private static final String USER_LOGIN_ENDPOINT = "/api/public/user/login";

    protected static final String EMAIL = "test@test.pl";

    protected static final String PASSWORD = "Start00!";

    private static final String ADMIN_EMAIL = "admin@rental-service.com";

    private static final String ADMIN_PASSWORD = "admin";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    private CommandLineRunner commandLineRunner;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    @BeforeEach
    void setUp() throws Exception {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, PASSWORD, "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        userService.validateAndRegisterUser(userRegisterDTO);
        commandLineRunner.run();
    }

    @AfterEach
    void cleanUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user");
    }

    protected MvcResult performLoginAction(final UserLoginDTO userLoginDTO) throws Exception {
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson).contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(request).andReturn();
    }

    protected String getAuthorizationTokenForAdmin() throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(ADMIN_EMAIL, ADMIN_PASSWORD);
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson).contentType(MediaType.APPLICATION_JSON);
        final MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        final AuthenticationResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthenticationResponse.class);

        return "Bearer " + authResponse.token();
    }

}
