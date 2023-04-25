package com.s1gawron.rentalservice.tool.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginRequest;
import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractToolControllerIntegrationTest {

    private static final String USER_LOGIN_ENDPOINT = "/api/public/user/login";

    private static final String CUSTOMER_EMAIL = "customer@test.pl";

    private static final String WORKER_EMAIL = "worker@test.pl";

    private static final String PASSWORD = "Start00!";

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    @Autowired
    protected ToolService toolService;

    @Autowired
    protected ToolRepository toolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest customerRegisterDTO = new UserRegisterRequest(CUSTOMER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final UserRegisterRequest workerRegisterDTO = new UserRegisterRequest(WORKER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.WORKER, null);

        userService.validateAndRegisterUser(customerRegisterDTO);
        userService.validateAndRegisterUser(workerRegisterDTO);
    }

    @AfterEach
    void cleanUp() {
        toolRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected String getAuthorizationToken(final UserRole userRole) throws Exception {
        if (userRole == UserRole.WORKER) {
            return getTokenFor(WORKER_EMAIL);
        }

        return getTokenFor(CUSTOMER_EMAIL);
    }

    private String getTokenFor(final String email) throws Exception {
        final UserLoginRequest userLoginRequest = new UserLoginRequest(email, PASSWORD);
        final String userLoginJson = objectMapper.writeValueAsString(userLoginRequest);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson).contentType(MediaType.APPLICATION_JSON);
        final MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        final AuthenticationResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthenticationResponse.class);

        return "Bearer " + authResponse.token();
    }

}
