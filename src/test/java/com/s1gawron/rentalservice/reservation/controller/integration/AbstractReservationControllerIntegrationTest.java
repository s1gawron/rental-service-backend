package com.s1gawron.rentalservice.reservation.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.repository.ReservationHasToolRepository;
import com.s1gawron.rentalservice.reservation.repository.ReservationRepository;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractReservationControllerIntegrationTest {

    protected static final String USER_LOGIN_ENDPOINT = "/api/public/user/login";

    protected static final String RESERVATION_ENDPOINT = "/api/customer/reservation/";

    protected static final String MAKE_RESERVATION_ENDPOINT = RESERVATION_ENDPOINT + "make";

    protected static final String CUSTOMER_EMAIL = "customer@test.pl";

    protected static final String DIFFERENT_CUSTOMER_EMAIL = "customer2@test.pl";

    protected static final String WORKER_EMAIL = "worker@test.pl";

    protected static final String PASSWORD = "Start00!";

    protected static final NoAccessForUserRoleException NO_ACCESS_FOR_USER_ROLE_EXCEPTION = NoAccessForUserRoleException.create("CUSTOMER RESERVATIONS");

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ToolRepository toolRepository;

    @Autowired
    protected ToolService toolService;

    @Autowired
    protected ReservationRepository reservationRepository;

    @Autowired
    protected ReservationHasToolRepository reservationHasToolRepository;

    @Autowired
    protected ReservationService reservationService;

    protected long currentToolId;

    protected long nextToolId;

    protected long currentReservationId;

    protected long loaderToolId;

    @BeforeEach
    @SneakyThrows
    @Transactional
    void setUp() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO customerRegisterDTO = new UserRegisterDTO(CUSTOMER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.CUSTOMER.getName(), addressDTO);
        final UserRegisterDTO differentCustomerRegisterDTO = new UserRegisterDTO(DIFFERENT_CUSTOMER_EMAIL, PASSWORD, "Tony", "Hawk",
            UserRole.CUSTOMER.getName(), addressDTO);
        final UserRegisterDTO workerRegisterDTO = new UserRegisterDTO(WORKER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.WORKER.getName(), null);

        userService.validateAndRegisterUser(customerRegisterDTO);
        userService.validateAndRegisterUser(differentCustomerRegisterDTO);
        userService.validateAndRegisterUser(workerRegisterDTO);

        final Tool hammer = ToolCreatorHelper.I.createTool();
        toolRepository.save(hammer);
        currentToolId = hammer.getToolId();

        final Tool chainsaw = ToolCreatorHelper.I.createChainsaw();
        toolRepository.save(chainsaw);
        nextToolId = chainsaw.getToolId();

        final Tool loader = ToolCreatorHelper.I.createLoader();
        toolRepository.save(loader);
        loaderToolId = loader.getToolId();
    }

    @AfterEach
    @Transactional
    void cleanUp() {
        reservationHasToolRepository.deleteAll();
        reservationRepository.deleteAll();
        toolRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected void performMakeReservationRequests() {
        final String hammerReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(2L) + "\",\n"
            + "  \"additionalComment\": \"Hammer\",\n"
            + "  \"toolIds\": [\n"
            + "    " + currentToolId + "\n"
            + "  ]\n"
            + "}";

        currentReservationId = performRequestAndCheckState(hammerReservationJson, getAuthorizationToken(CUSTOMER_EMAIL));

        final String chainsawReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(5L) + "\",\n"
            + "  \"additionalComment\": \"Chainsaw\",\n"
            + "  \"toolIds\": [\n"
            + "    " + nextToolId + "\n"
            + "  ]\n"
            + "}";

        performRequestAndCheckState(chainsawReservationJson, getAuthorizationToken(CUSTOMER_EMAIL));
    }

    @SneakyThrows
    protected long performRequestAndCheckState(final String objectJson, final String authorizationToken) {
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(objectJson).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", authorizationToken);

        final MockHttpServletResponse result = mockMvc.perform(request).andReturn().getResponse();
        final int status = result.getStatus();

        if (status != 200) {
            throw new IllegalStateException("Making reservation was not successful!");
        }

        final ReservationDetailsDTO reservationDetailsDTO = objectMapper.readValue(result.getContentAsString(), ReservationDetailsDTO.class);

        return reservationDetailsDTO.getReservationId();
    }

    @SneakyThrows
    protected String getAuthorizationToken(final String email) {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(email, PASSWORD);
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson);
        final MvcResult loginResult = mockMvc.perform(request).andReturn();

        return loginResult.getResponse().getHeader("Authorization");
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

    @Transactional(readOnly = true)
    protected Reservation getReservationDetails(final long reservationId) {
        return reservationRepository.findByReservationId(reservationId).orElseThrow(() -> ReservationNotFoundException.create(reservationId));
    }

}
