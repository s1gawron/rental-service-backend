package com.s1gawron.rentalservice.tool.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractToolControllerIntegrationTest {

    private static final String USER_LOGIN_ENDPOINT = "/api/public/user/v1/login";

    private static final String CUSTOMER_EMAIL = "customer@test.pl";

    private static final String WORKER_EMAIL = "worker@test.pl";

    private static final String PASSWORD = "Start00!";

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    @Autowired
    protected ToolService toolService;

    @Autowired
    protected ToolDAO toolDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO customerRegisterDTO = new UserRegisterDTO(CUSTOMER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        userService.validateAndRegisterUser(customerRegisterDTO);

        final UserRegisterDTO workerRegisterDTO = new UserRegisterDTO(WORKER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.WORKER, null);
        final String workerEncodedPassword = passwordEncoder.encode(PASSWORD);
        final User workerUser = User.createUser(workerRegisterDTO, workerEncodedPassword);
        userService.saveUser(workerUser);
    }

    @AfterEach
    void cleanUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tool", "user");
    }

    protected String getAuthorizationToken(final UserRole userRole) throws Exception {
        if (userRole == UserRole.WORKER) {
            return getTokenFor(WORKER_EMAIL);
        }

        return getTokenFor(CUSTOMER_EMAIL);
    }

    protected void saveToolsForTest(final List<Tool> tools) {
        tools.forEach(this::saveToolForTest);
    }

    protected void saveToolForTest(final Tool tool) {
        toolDAO.save(tool);
    }

    private String getTokenFor(final String email) throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(email, PASSWORD);
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson).contentType(MediaType.APPLICATION_JSON);
        final MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        final AuthenticationResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthenticationResponse.class);

        return "Bearer " + authResponse.token();
    }

}
