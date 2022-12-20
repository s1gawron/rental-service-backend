package com.s1gawron.rentalservice.user.controller.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.jwt.JwtConfig;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractUserControllerTest {

    private static final AddressDTO ADDRESS_DTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");

    private static final UserRegisterDTO USER_REGISTER_DTO = new UserRegisterDTO("test@test.pl", "Start00!", "John", "Kowalski", "CUSTOMER", ADDRESS_DTO);

    @MockBean
    private DataSource dataSourceMock;

    @MockBean
    private JwtConfig jwtConfigMock;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserService userServiceMock;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected String userRegisterJson;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        userRegisterJson = objectMapper.writeValueAsString(USER_REGISTER_DTO);
    }

    protected void assertErrorResponse(final HttpStatus expectedStatus, final String expectedMessage, final String expectedUri,
        final ErrorResponse actualErrorResponse) {
        assertEquals(expectedStatus.value(), actualErrorResponse.getCode());
        assertEquals(expectedStatus.getReasonPhrase(), actualErrorResponse.getError());
        assertEquals(expectedMessage, actualErrorResponse.getMessage());
        assertEquals(expectedUri, actualErrorResponse.getURI());
    }

    @SneakyThrows
    protected ErrorResponse toErrorResponse(final String responseMessage) {
        return objectMapper.readValue(responseMessage, ErrorResponse.class);
    }

}
