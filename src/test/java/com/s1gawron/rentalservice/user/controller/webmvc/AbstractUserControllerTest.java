package com.s1gawron.rentalservice.user.controller.webmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.security.JwtService;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserAuthenticationService;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

abstract class AbstractUserControllerTest {

    private static final AddressDTO ADDRESS_DTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");

    private static final UserRegisterDTO USER_REGISTER_DTO = new UserRegisterDTO("test@test.pl", "Start00!", "John", "Kowalski", UserRole.CUSTOMER,
        ADDRESS_DTO);

    protected static final String ERROR_RESPONSE_MESSAGE_PLACEHOLDER = "$.message";

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserService userServiceMock;

    @MockBean
    protected JwtService jwtServiceMock;

    @MockBean
    protected UserAuthenticationService userAuthenticationService;

    protected final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    protected String userRegisterJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        userRegisterJson = objectMapper.writeValueAsString(USER_REGISTER_DTO);
    }

}
