package com.s1gawron.rentalservice.reservation.service;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.helper.ReservationCreatorHelper;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.reservation.repository.ReservationHasToolRepository;
import com.s1gawron.rentalservice.reservation.repository.ReservationRepository;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.helper.UserCreatorHelper;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private static final String USER_EMAIL = "test@test.pl";

    private SecurityContext securityContextMock;

    private ReservationRepository reservationRepositoryMock;

    private ReservationHasToolRepository reservationHasToolRepositoryMock;

    private UserService userServiceMock;

    private ToolService toolServiceMock;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        final Authentication authentication = Mockito.mock(Authentication.class);
        securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(USER_EMAIL);
        SecurityContextHolder.setContext(securityContextMock);

        reservationRepositoryMock = Mockito.mock(ReservationRepository.class);
        reservationHasToolRepositoryMock = Mockito.mock(ReservationHasToolRepository.class);
        userServiceMock = Mockito.mock(UserService.class);
        toolServiceMock = Mockito.mock(ToolService.class);
        reservationService = new ReservationService(reservationRepositoryMock, reservationHasToolRepositoryMock, userServiceMock, toolServiceMock);
    }

    @Test
    void shouldGetUserReservations() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final List<Reservation> reservations = ReservationCreatorHelper.I.createReservations();
        final List<ToolDetailsDTO> toolDetails = ToolCreatorHelper.I.createToolDTOList();

        reservations.forEach(customer::addReservation);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(reservationRepositoryMock.findAllByCustomer(customer)).thenReturn(reservations);

        for (int i = 0; i < toolDetails.size(); i++) {
            Mockito.when(toolServiceMock.getToolDetailsByReservationHasTools(reservations.get(i).getReservationHasTools()))
                .thenReturn(List.of(toolDetails.get(i)));
        }

        final List<ReservationDetailsDTO> result = reservationService.getUserReservations();

        assertNotNull(result);
        assertEquals(3, result.size());

        for (ReservationDetailsDTO reservationDetailsDTO : result) {
            assertEquals(1, reservationDetailsDTO.getTools().size());
        }
    }

    @Test
    void shouldThrowExceptionWhenUserIsWorkerWhileGettingUserReservations() {
        final User worker = UserCreatorHelper.I.createWorker();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(worker));

        assertThrows(NoAccessForUserRoleException.class, () -> reservationService.getUserReservations(),
            "Current user role is not allowed to use module#CUSTOMER RESERVATIONS!");
    }

    @Test
    void shouldGetReservationDetails() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();

        customer.addReservation(reservation);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(reservationRepositoryMock.findByReservationId(1L)).thenReturn(Optional.of(reservation));
        Mockito.when(toolServiceMock.getToolDetailsByReservationHasTools(reservation.getReservationHasTools())).thenReturn(List.of(toolDetailsDTO));

        final ReservationDetailsDTO result = reservationService.getReservationDetails(1L);

        assertEquals(LocalDate.now(), result.getDateFrom());
        assertEquals(LocalDate.now().plusDays(3L), result.getDateTo());
        assertEquals("Hammer", result.getAdditionalComment());
        assertEquals(1, result.getTools().size());
        assertToolDetailsDTO(toolDetailsDTO, result.getTools().get(0));
    }

    @Test
    void shouldThrowExceptionWhenUserIsWorkerWhileGettingReservationDetails() {
        final User worker = UserCreatorHelper.I.createWorker();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(worker));

        assertThrows(NoAccessForUserRoleException.class, () -> reservationService.getReservationDetails(1L),
            "Current user role is not allowed to use module#CUSTOMER RESERVATIONS!");
    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotBelongToUserWhileGettingReservationDetails() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final User differentCustomer = UserCreatorHelper.I.createDifferentCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();
        final Reservation differentReservation = ReservationCreatorHelper.I.createDifferentReservation();

        customer.addReservation(reservation);
        differentCustomer.addReservation(differentReservation);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(reservationRepositoryMock.findByReservationId(1L)).thenReturn(Optional.of(reservation));

        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationDetails(2L),
            "Reservation#2 was not found!");
    }

    @Test
    void shouldThrowExceptionWhenReservationIsNotFound() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();

        customer.addReservation(reservation);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(reservationRepositoryMock.findByReservationId(1L)).thenThrow(ReservationNotFoundException.create(1L));

        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationDetails(1L), "Reservation#1 was not found!");
    }

    @Test
    void shouldMakeReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User customer = UserCreatorHelper.I.createCustomer();
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(toolServiceMock.getToolById(1L)).thenReturn(tools.get(0));
        Mockito.when(toolServiceMock.getToolById(2L)).thenReturn(tools.get(1));
        makeToolUnavailable(tools.get(0));
        makeToolUnavailable(tools.get(1));
        Mockito.when(reservationRepositoryMock.save(Mockito.any(Reservation.class))).thenReturn(ReservationCreatorHelper.I.createDifferentReservation());

        final ReservationDetailsDTO result = reservationService.makeReservation(reservationDTO);

        Mockito.verify(reservationHasToolRepositoryMock, Mockito.times(2)).save(Mockito.any(ReservationHasTool.class));
        Mockito.verify(toolServiceMock, Mockito.times(2)).makeToolUnavailableAndSave(Mockito.any(Tool.class));
        Mockito.verify(reservationRepositoryMock, Mockito.times(1)).save(Mockito.any(Reservation.class));
        Mockito.verify(userServiceMock, Mockito.times(1)).saveCustomerWithReservation(Mockito.any(User.class));
        assertEquals(LocalDate.now(), result.getDateFrom());
        assertEquals(LocalDate.now().plusDays(1L), result.getDateTo());
        assertEquals(BigDecimal.valueOf(1011.98), result.getReservationFinalPrice());
        assertEquals("Hammer and loader", result.getAdditionalComment());
        assertToolDetailsDTO(tools.get(0).toToolDetailsDTO(), result.getTools().get(0));
        assertToolDetailsDTO(tools.get(1).toToolDetailsDTO(), result.getTools().get(1));
    }

    private void makeToolUnavailable(final Tool tool) {
        Mockito.doAnswer(invocation -> {
            tool.makeToolUnavailable();
            return null;
        }).when(toolServiceMock).makeToolUnavailableAndSave(tool);
    }

    @Test
    void shouldThrowExceptionWhenReservationHasEmptyPropertiesWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(null, LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));

        assertThrows(ReservationEmptyPropertiesException.class, () -> reservationService.makeReservation(reservationDTO), "Date from cannot be null!");
    }

    @Test
    void shouldThrowExceptionWhenDueDateIsBeforeCurrentTimeWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().minusDays(3L), "Hammer and loader", List.of(1L, 2L));

        assertThrows(DateMismatchException.class, () -> reservationService.makeReservation(reservationDTO), "Due date cannot be before current date!");
    }

    @Test
    void shouldThrowExceptionWhenUserIsWorkerWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User worker = UserCreatorHelper.I.createWorker();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(worker));

        assertThrows(NoAccessForUserRoleException.class, () -> reservationService.makeReservation(reservationDTO),
            "Current user role is not allowed to use module#CUSTOMER RESERVATIONS!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotFoundWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.doThrow(ToolNotFoundException.create(1L)).when(toolServiceMock).isToolAvailable(1L);

        assertThrows(ToolNotFoundException.class, () -> reservationService.makeReservation(reservationDTO), "Tool#1 could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotAvailableWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.doThrow(ToolUnavailableException.create(1L)).when(toolServiceMock).isToolAvailable(1L);

        assertThrows(ToolUnavailableException.class, () -> reservationService.makeReservation(reservationDTO), "Tool#1 is unavailable!");
    }

    @Test
    void shouldCancelReservation() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();
        final Tool unavailableTool = ToolCreatorHelper.I.createUnavailableTool();

        customer.addReservation(reservation);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(reservationRepositoryMock.findByReservationId(1L)).thenReturn(Optional.of(reservation));
        Mockito.when(toolServiceMock.getToolsByReservationHasTools(reservation.getReservationHasTools())).thenReturn(List.of(unavailableTool));
        makeToolAvailable(unavailableTool);

        final ReservationDetailsDTO result = reservationService.cancelReservation(1L);

        assertEquals(LocalDate.now(), result.getDateFrom());
        assertEquals(LocalDate.now().plusDays(3L), result.getDateTo());
        assertTrue(result.isCanceled());
        assertEquals("Hammer", result.getAdditionalComment());
        assertEquals(1, result.getTools().size());
        assertTrue(result.getTools().get(0).getAvailable());
    }

    private void makeToolAvailable(final Tool unavailableTool) {
        Mockito.doAnswer(invocation -> {
            unavailableTool.makeToolAvailable();
            return null;
        }).when(toolServiceMock).makeToolAvailableAndSave(unavailableTool);
    }

    @Test
    void shouldThrowExceptionWhenUserIsWorkerWhileCancelingReservation() {
        final User worker = UserCreatorHelper.I.createWorker();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(worker));

        assertThrows(NoAccessForUserRoleException.class, () -> reservationService.cancelReservation(1L),
            "Current user role is not allowed to use module#CUSTOMER RESERVATIONS!");
    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotBelongToUserWhileCancelingReservation() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final User differentCustomer = UserCreatorHelper.I.createDifferentCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();
        final Reservation differentReservation = ReservationCreatorHelper.I.createDifferentReservation();

        customer.addReservation(reservation);
        differentCustomer.addReservation(differentReservation);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(reservationRepositoryMock.findByReservationId(1L)).thenReturn(Optional.of(reservation));

        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancelReservation(2L),
            "Reservation#2 was not found!");
    }

    @Test
    void shouldThrowExceptionWhenReservationIsNotFoundWhileCancelingReservation() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();

        customer.addReservation(reservation);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(customer));
        Mockito.when(reservationRepositoryMock.findByReservationId(1L)).thenThrow(ReservationNotFoundException.create(1L));

        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancelReservation(1L), "Reservation#1 was not found!");
    }

    @Test
    void shouldGetReservationsIds() {
        final List<Long> reservationIds = List.of(1L, 2L, 3L);

        Mockito.when(reservationRepositoryMock.getAllIds()).thenReturn(reservationIds);

        final List<Long> result = reservationService.getReservationIds();

        assertEquals(3, result.size());
    }

    @Test
    void shouldCheckReservationExpiryStatus() {
        final Reservation reservation = ReservationCreatorHelper.I.createReservationForExpiry();
        final Tool tool = ToolCreatorHelper.I.createUnavailableTool();

        Mockito.when(reservationRepositoryMock.findAllById(List.of(1L))).thenReturn(List.of(reservation));
        Mockito.when(toolServiceMock.getToolsByReservationHasTools(reservation.getReservationHasTools())).thenReturn(List.of(tool));
        makeToolAvailable(tool);

        reservationService.checkReservationsExpiryStatus(List.of(1L));

        assertTrue(tool.isAvailable());
        assertTrue(reservation.isExpired());
    }

    private void assertToolDetailsDTO(final ToolDetailsDTO expected, final ToolDetailsDTO resultTool) {
        assertEquals(expected.getName(), resultTool.getName());
        assertEquals(expected.getAvailable(), resultTool.getAvailable());
        assertEquals(expected.getDescription(), resultTool.getDescription());
        assertEquals(expected.getToolCategory(), resultTool.getToolCategory());
        assertEquals(expected.getPrice(), resultTool.getPrice());
        assertEquals(expected.getToolState().getStateType(), resultTool.getToolState().getStateType());
    }

}