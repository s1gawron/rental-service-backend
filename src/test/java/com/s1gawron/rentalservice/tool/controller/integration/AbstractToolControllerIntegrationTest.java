package com.s1gawron.rentalservice.tool.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
        final UserRegisterDTO customerRegisterDTO = new UserRegisterDTO(CUSTOMER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.CUSTOMER.name(), addressDTO);
        final UserRegisterDTO workerRegisterDTO = new UserRegisterDTO(WORKER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.WORKER.name(), null);

        userService.validateAndRegisterUser(customerRegisterDTO);
        userService.validateAndRegisterUser(workerRegisterDTO);
    }

    @AfterEach
    void cleanUp() {
        toolRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected String getCustomerAuthorizationToken() throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(CUSTOMER_EMAIL, PASSWORD);
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson);
        final MvcResult loginResult = mockMvc.perform(request).andReturn();

        return loginResult.getResponse().getHeader("Authorization");
    }

    protected String getWorkerAuthorizationToken() throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(WORKER_EMAIL, PASSWORD);
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson);
        final MvcResult loginResult = mockMvc.perform(request).andReturn();

        return loginResult.getResponse().getHeader("Authorization");
    }

}
