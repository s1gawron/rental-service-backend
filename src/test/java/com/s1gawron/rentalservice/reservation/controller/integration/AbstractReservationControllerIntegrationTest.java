package com.s1gawron.rentalservice.reservation.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.repository.ReservationDAO;
import com.s1gawron.rentalservice.reservation.repository.ReservationHasToolDAO;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserDAO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractReservationControllerIntegrationTest {

    protected static final String ERROR_RESPONSE_MESSAGE_PLACEHOLDER = "$.message";

    protected static final String USER_LOGIN_ENDPOINT = "/api/public/user/login";

    protected static final String RESERVATION_ENDPOINT = "/api/customer/reservation/";

    protected static final String MAKE_RESERVATION_ENDPOINT = RESERVATION_ENDPOINT + "make";

    protected static final String CUSTOMER_EMAIL = "customer@test.pl";

    protected static final String DIFFERENT_CUSTOMER_EMAIL = "customer2@test.pl";

    protected static final String WORKER_EMAIL = "worker@test.pl";

    protected static final String PASSWORD = "Start00!";

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    @Autowired
    protected UserDAO userDAO;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ToolDAO toolDAO;

    @Autowired
    protected ToolService toolService;

    @Autowired
    protected ReservationDAO reservationDAO;

    @Autowired
    protected ReservationHasToolDAO reservationHasToolDAO;

    @Autowired
    protected ReservationService reservationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected long currentToolId;

    protected long nextToolId;

    protected long currentReservationId;

    protected long loaderToolId;

    protected long removedToolId;

    @BeforeEach
    @Transactional
    void setUp() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO customerRegisterDTO = new UserRegisterDTO(CUSTOMER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final UserRegisterDTO differentCustomerRegisterDTO = new UserRegisterDTO(DIFFERENT_CUSTOMER_EMAIL, PASSWORD, "Tony", "Hawk", UserRole.CUSTOMER,
            addressDTO);

        userService.validateAndRegisterUser(customerRegisterDTO);
        userService.validateAndRegisterUser(differentCustomerRegisterDTO);

        final UserRegisterDTO workerRegisterDTO = new UserRegisterDTO(WORKER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.WORKER, null);
        final String workerEncodedPassword = passwordEncoder.encode(PASSWORD);
        final User workerUser = User.createUser(workerRegisterDTO, workerEncodedPassword);
        userService.saveUser(workerUser);

        final Tool hammer = ToolCreatorHelper.I.createTool();
        currentToolId = toolDAO.save(hammer).getToolId();

        final Tool chainsaw = ToolCreatorHelper.I.createChainsaw();
        nextToolId = toolDAO.save(chainsaw).getToolId();

        final Tool loader = ToolCreatorHelper.I.createLoader();
        loaderToolId = toolDAO.save(loader).getToolId();

        final Tool removedHammer = ToolCreatorHelper.I.createRemovedHammerWithAvailability();
        removedToolId = toolDAO.save(removedHammer).getToolId();
    }

    @AfterEach
    @Transactional
    void cleanUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "reservation_has_tool", "reservation", "tool", "user");
    }

    protected void performMakeReservationRequests() throws Exception {
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

    protected long performRequestAndCheckState(final String objectJson, final String authorizationToken) throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(objectJson).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", authorizationToken);

        final MockHttpServletResponse result = mockMvc.perform(request).andReturn().getResponse();
        final int status = result.getStatus();

        if (status != 200) {
            throw new IllegalStateException("Making reservation was not successful!");
        }

        final ReservationDetailsDTO reservationDetailsDTO = objectMapper.readValue(result.getContentAsString(), ReservationDetailsDTO.class);

        return reservationDetailsDTO.reservationId();
    }

    protected String getAuthorizationToken(final String email) throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(email, PASSWORD);
        final String userLoginJson = objectMapper.writeValueAsString(userLoginDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_LOGIN_ENDPOINT).content(userLoginJson).contentType(MediaType.APPLICATION_JSON);
        final MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        final AuthenticationResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthenticationResponse.class);

        return "Bearer " + authResponse.token();
    }

    protected Reservation getReservationDetails(final long reservationId) {
        return reservationDAO.findByReservationId(reservationId).orElseThrow(() -> ReservationNotFoundException.create(reservationId));
    }

}
