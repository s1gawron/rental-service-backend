package com.s1gawron.rentalservice.user.controller.webmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.configuration.jwt.JwtService;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import com.s1gawron.rentalservice.user.service.AuthenticationService;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

abstract class AbstractUserControllerTest {

    private static final AddressDTO ADDRESS_DTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");

    private static final UserRegisterRequest USER_REGISTER_DTO = new UserRegisterRequest("test@test.pl", "Start00!", "John", "Kowalski", "CUSTOMER",
        ADDRESS_DTO);

    protected static final String ERROR_RESPONSE_MESSAGE_PLACEHOLDER = "$.message";

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserService userServiceMock;

    @MockBean
    protected JwtService jwtServiceMock;

    @MockBean
    protected AuthenticationService authenticationService;

    protected final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    protected String userRegisterJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        userRegisterJson = objectMapper.writeValueAsString(USER_REGISTER_DTO);
    }

}
