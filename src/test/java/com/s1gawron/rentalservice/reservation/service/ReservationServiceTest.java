package com.s1gawron.rentalservice.reservation.service;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.helper.ReservationCreatorHelper;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.repository.ReservationDAO;
import com.s1gawron.rentalservice.reservationtool.model.ReservationTool;
import com.s1gawron.rentalservice.reservationtool.service.ReservationToolService;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolRemovedException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import com.s1gawron.rentalservice.shared.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.shared.helper.UserCreatorHelper;
import com.s1gawron.rentalservice.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 25);

    private Authentication authenticationMock;

    private ReservationDAO reservationDAO;

    private ReservationToolService reservationToolService;

    private ToolService toolServiceMock;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        authenticationMock = Mockito.mock(Authentication.class);
        final SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        SecurityContextHolder.setContext(securityContextMock);

        reservationDAO = Mockito.mock(ReservationDAO.class);
        reservationToolService = Mockito.mock(ReservationToolService.class);
        toolServiceMock = Mockito.mock(ToolService.class);
        reservationService = new ReservationService(reservationDAO, reservationToolService, toolServiceMock);
    }

    @Test
    void shouldGetUserReservations() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final List<Reservation> reservations = ReservationCreatorHelper.I.createReservations();
        final PageImpl<Reservation> reservationPage = new PageImpl<>(reservations);

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(reservationDAO.findAllByCustomer(customer, DEFAULT_PAGEABLE)).thenReturn(reservationPage);
        final ReservationListingDTO result = reservationService.getUserReservations(DEFAULT_PAGEABLE);

        assertNotNull(result);
        assertEquals(1, result.numberOfPages());
        assertEquals(3, result.totalNumberOfReservations());

        for (ReservationDetailsDTO reservationDetailsDTO : result.reservations()) {
            assertEquals(1, reservationDetailsDTO.tools().size());
        }
    }

    @Test
    void shouldGetReservationDetails() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(reservationDAO.findByReservationIdAndCustomer(1L, customer)).thenReturn(Optional.of(reservation));

        final ReservationDetailsDTO result = reservationService.getReservationDetails(1L);

        assertEquals(LocalDate.now(), result.dateFrom());
        assertEquals(LocalDate.now().plusDays(3L), result.dateTo());
        assertEquals("Hammer", result.additionalComment());
        assertEquals(1, result.tools().size());
        assertToolDetailsDTO(toolDetailsDTO, result.tools().get(0));
    }

    @Test
    void shouldThrowExceptionWhenReservationIsNotFound() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(reservationDAO.findByReservationIdAndCustomer(1L, customer)).thenThrow(ReservationNotFoundException.create(1L));

        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationDetails(1L), "Reservation#1 was not found!");
    }

    @Test
    void shouldMakeReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User customer = UserCreatorHelper.I.createCustomer();
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(toolServiceMock.getToolById(1L)).thenReturn(tools.get(0));
        Mockito.when(toolServiceMock.getToolById(2L)).thenReturn(tools.get(1));
        makeToolUnavailable(tools.get(0));
        makeToolUnavailable(tools.get(1));
        Mockito.when(reservationDAO.save(Mockito.any(Reservation.class))).thenReturn(ReservationCreatorHelper.I.createDifferentReservation());

        final ReservationDetailsDTO result = reservationService.makeReservation(reservationDTO);

        Mockito.verify(reservationToolService, Mockito.times(1)).saveAll(Mockito.anyList());
        Mockito.verify(toolServiceMock, Mockito.times(2)).makeToolUnavailableAndSave(Mockito.any(Tool.class));
        Mockito.verify(reservationDAO, Mockito.times(1)).save(Mockito.any(Reservation.class));
        assertEquals(LocalDate.now(), result.dateFrom());
        assertEquals(LocalDate.now().plusDays(1L), result.dateTo());
        assertEquals(BigDecimal.valueOf(1011.98), result.reservationFinalPrice());
        assertEquals("Hammer and loader", result.additionalComment());
        assertToolDetailsDTO(tools.get(0).toToolDetailsDTO(), result.tools().get(0));
        assertToolDetailsDTO(tools.get(1).toToolDetailsDTO(), result.tools().get(1));
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
    void shouldThrowExceptionWhenToolIsNotFoundWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.doThrow(ToolNotFoundException.create(1L)).when(toolServiceMock).isToolAvailableOrRemoved(1L);

        assertThrows(ToolNotFoundException.class, () -> reservationService.makeReservation(reservationDTO), "Tool#1 could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotAvailableWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.doThrow(ToolUnavailableException.create(1L)).when(toolServiceMock).isToolAvailableOrRemoved(1L);

        assertThrows(ToolUnavailableException.class, () -> reservationService.makeReservation(reservationDTO), "Tool#1 is unavailable!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsRemovedWhileMakingReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(1L), "Hammer and loader", List.of(1L, 2L));
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.doThrow(ToolRemovedException.create(1L)).when(toolServiceMock).isToolAvailableOrRemoved(1L);

        assertThrows(ToolRemovedException.class, () -> reservationService.makeReservation(reservationDTO), "Tool#1 is removed!");
    }

    @Test
    void shouldCancelReservation() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();
        final List<Tool> toolsOnReservation = reservation.getReservationTools().stream()
            .map(ReservationTool::getTool)
            .toList();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(reservationDAO.findByReservationIdAndCustomer(1L, customer)).thenReturn(Optional.of(reservation));

        final ReservationDetailsDTO result = reservationService.cancelReservation(1L);

        assertEquals(LocalDate.now(), result.dateFrom());
        assertEquals(LocalDate.now().plusDays(3L), result.dateTo());
        assertTrue(result.canceled());
        assertEquals("Hammer", result.additionalComment());
        assertEquals(1, result.tools().size());
        assertTrue(result.tools().get(0).available());
        assertTrue(toolsOnReservation.get(0).isAvailable());
    }

    @Test
    void shouldThrowExceptionWhenReservationIsNotFoundWhileCancelingReservation() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(reservationDAO.findByReservationIdAndCustomer(1L, customer)).thenThrow(ReservationNotFoundException.create(1L));

        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancelReservation(1L), "Reservation#1 was not found!");
    }

    @Test
    void shouldExpireReservation() {
        final Reservation reservation = ReservationCreatorHelper.I.createReservationForExpiry();
        final List<Tool> toolsOnReservation = reservation.getReservationTools().stream()
            .map(ReservationTool::getTool)
            .toList();

        Mockito.when(reservationDAO.findByReservationId(1L)).thenReturn(Optional.of(reservation));

        reservationService.expireReservation(1L);

        assertTrue(toolsOnReservation.get(0).isAvailable());
        assertTrue(reservation.isExpired());
    }

    private void assertToolDetailsDTO(final ToolDetailsDTO expected, final ToolDetailsDTO resultTool) {
        assertEquals(expected.name(), resultTool.name());
        assertEquals(expected.available(), resultTool.available());
        assertEquals(expected.description(), resultTool.description());
        assertEquals(expected.toolCategory(), resultTool.toolCategory());
        assertEquals(expected.price(), resultTool.price());
        assertEquals(expected.toolState().stateType(), resultTool.toolState().stateType());
    }

}