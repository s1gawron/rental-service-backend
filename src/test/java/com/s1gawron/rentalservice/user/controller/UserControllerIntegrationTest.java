package com.s1gawron.rentalservice.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    private static final String EMAIL = "test@test.pl";

    private static final String PASSWORD = "Start00!";

    private static final String USER_REGISTER_ENDPOINT = "/api/user/register";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    void shouldLoginAndReturnValidTokenInHeader() {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(EMAIL, PASSWORD);

        final MvcResult result = performLoginAction(userLoginDTO);
        final String token = result.getResponse().getHeader("Authorization");

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer"));
    }

    @Test
    void shouldReturnUnauthorizedStatus() {
        final UserLoginDTO userLoginDTO = new UserLoginDTO("testUser", "wrongPassword");

        final MvcResult result = performLoginAction(userLoginDTO);
        final String token = result.getResponse().getHeader("Authorization");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
        assertNull(token);
    }

    @SneakyThrows
    private MvcResult performLoginAction(final UserLoginDTO userLoginDTO) {
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post("/api/user/login").content(userLoginJson);

        return mockMvc.perform(request).andReturn();
    }

    @Test
    @SneakyThrows
    void shouldRegisterUser() {
        final String json = "{\n"
            + "  \"email\": \"new@test.pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"CUSTOMER\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new@test.pl").isPresent());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenUserRoleDoesNotExist() {
        final String json = "{\n"
            + "  \"email\": \"no-role@test.pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("no-role@test.pl").isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenEmailIsEmpty() {
        final String json = "{\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenEmailPatternIsWrong() {
        final String json = "{\n"
            + "  \"email\": \"new-test-pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new-test-pl").isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenPasswordIsWeak() {
        final String json = "{\n"
            + "  \"email\": \"weak-password@test.pl\",\n"
            + "  \"password\": \"password\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"CUSTOMER\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("weak-password@test.pl").isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenPostCodePatternIsWrong() {
        final String json = "{\n"
            + "  \"email\": \"wrong-post-code@test.pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"0100\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("wrong-post-code@test.pl").isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetUserDetails() {
        final UserDTO expectedUserDto = userService.getUserByEmail(EMAIL)
            .orElseThrow(() -> new IllegalStateException("Expected user dto cannot be null!"))
            .toUserDTO();
        final UserLoginDTO userLoginDTO = new UserLoginDTO(EMAIL, PASSWORD);
        final MvcResult loginResult = performLoginAction(userLoginDTO);
        final String token = loginResult.getResponse().getHeader("Authorization");
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/user/details").header("Authorization", token);

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final UserDTO userDTOResult = objectMapper.readValue(jsonResult, UserDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(userDTOResult);
        assertEquals(expectedUserDto.getEmail(), userDTOResult.getEmail());
        assertEquals(expectedUserDto.getFirstName(), userDTOResult.getFirstName());
        assertEquals(expectedUserDto.getLastName(), userDTOResult.getLastName());
        assertEquals(expectedUserDto.getUserRole(), userDTOResult.getUserRole());
        assertEquals(expectedUserDto.getCustomerAddress().getCountry(), userDTOResult.getCustomerAddress().getCountry());
        assertEquals(expectedUserDto.getCustomerAddress().getPostCode(), userDTOResult.getCustomerAddress().getPostCode());
    }

    @Test
    @SneakyThrows
    void shouldNotGetUserDetailsAndReturnForbiddenWhenUserIsNotLoggedIn() {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/user/details");
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

}
