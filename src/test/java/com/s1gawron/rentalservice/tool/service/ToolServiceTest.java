package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.reservation.helper.ReservationCreatorHelper;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.shared.UserUnauthenticatedException;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.repository.ToolStateRepository;
import com.s1gawron.rentalservice.user.helper.UserCreatorHelper;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ToolServiceTest {

    private static final String ERROR_MESSAGE = "User: test@test.pl could not be found!";

    private static final String UNAUTHENTICATED_ERROR_MESSAGE = "User is not authenticated!";

    private static final String USER_NOT_ALLOWED_ERROR_MESSAGE = "Current user role is not allowed to use: TOOL MANAGEMENT module!";

    private Authentication authenticationMock;

    private SecurityContext securityContextMock;

    private ToolRepository toolRepositoryMock;

    private ToolStateRepository toolStateRepositoryMock;

    private UserService userServiceMock;

    private ToolService toolService;

    @BeforeEach
    void setUp() {
        authenticationMock = Mockito.mock(Authentication.class);
        securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        SecurityContextHolder.setContext(securityContextMock);

        toolRepositoryMock = Mockito.mock(ToolRepository.class);
        toolStateRepositoryMock = Mockito.mock(ToolStateRepository.class);
        userServiceMock = Mockito.mock(UserService.class);
        toolService = new ToolService(toolRepositoryMock, toolStateRepositoryMock, userServiceMock);
    }

    @Test
    void shouldGetToolsByCategory() {
        final List<Tool> heavyTools = ToolCreatorHelper.I.createToolList().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .toList();

        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.HEAVY)).thenReturn(heavyTools);

        final ToolListingDTO result = toolService.getToolsByCategory("heavy");

        assertEquals(2, result.count());
        assertEquals(2, result.tools().size());
        result.tools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY.name(), toolDTO.toolCategory()));
    }

    @Test
    void shouldNotGetHeavyToolsByLightCategory() {
        final List<Tool> lightTools = ToolCreatorHelper.I.createHeavyTools().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.LIGHT))
            .toList();

        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.LIGHT)).thenReturn(lightTools);

        final ToolListingDTO result = toolService.getToolsByCategory("light");

        assertEquals(0, result.count());
        assertEquals(0, result.tools().size());
    }

    @Test
    void shouldThrowExceptionWhenCategoryDoesNotExist() {
        final String categoryDoesNotExistName = "medium";

        assertThrows(ToolCategoryDoesNotExistException.class, () -> toolService.getToolsByCategory(categoryDoesNotExistName),
            "Category: " + categoryDoesNotExistName + " does not exist!");
    }

    @Test
    void shouldGetNewTools() {
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();

        Mockito.when(toolRepositoryMock.findNewTools()).thenReturn(tools);

        final List<ToolDetailsDTO> expectedTools = ToolCreatorHelper.I.createToolDTOList();
        final List<ToolDetailsDTO> result = toolService.getNewTools();

        assertEquals(3, result.size());
        assertTools(expectedTools, result);
    }

    @Test
    void shouldGetToolById() {
        final Tool tool = ToolCreatorHelper.I.createTool();

        Mockito.when(toolRepositoryMock.findById(1L)).thenReturn(Optional.of(tool));

        final ToolDetailsDTO expected = tool.toToolDetailsDTO();
        final ToolDetailsDTO result = toolService.getToolDetails(1L);

        assertToolDetailsDTO(expected, result);
    }

    @Test
    void shouldThrowExceptionWhenToolIsEmpty() {
        assertThrows(ToolNotFoundException.class, () -> toolService.getToolDetails(1L), "Tool: 1 could not be found!");
    }

    @Test
    void shouldValidateAndAddTool() {
        final User worker = UserCreatorHelper.I.createWorker();
        final ToolDTO expected = ToolCreatorHelper.I.createToolDTO();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(worker);
        Mockito.when(userServiceMock.getUserByEmail(worker.getEmail())).thenReturn(Optional.of(worker));

        final ToolDetailsDTO result = toolService.validateAndAddTool(expected);

        Mockito.verify(toolStateRepositoryMock, Mockito.times(1)).save(Mockito.any(ToolState.class));
        Mockito.verify(toolRepositoryMock, Mockito.times(1)).save(Mockito.any(Tool.class));
        assertToolDTO(expected, result);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistWhileAddingTool() {
        final User worker = UserCreatorHelper.I.createWorker();
        final ToolDTO expected = ToolCreatorHelper.I.createToolDTO();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(worker);

        assertThrows(UserNotFoundException.class, () -> toolService.validateAndAddTool(expected), ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAllowedToAddTool() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final ToolDTO expected = ToolCreatorHelper.I.createToolDTO();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(userServiceMock.getUserByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        assertThrows(NoAccessForUserRoleException.class, () -> toolService.validateAndAddTool(expected), USER_NOT_ALLOWED_ERROR_MESSAGE);
    }

    @Test
    void shouldValidateAndEditTool() {
        final User worker = UserCreatorHelper.I.createWorker();
        final Tool originalTool = ToolCreatorHelper.I.createTool();
        final ToolDetailsDTO editedTool = ToolCreatorHelper.I.createEditedToolDTO();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(worker);
        Mockito.when(userServiceMock.getUserByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        Mockito.when(toolRepositoryMock.findById(1L)).thenReturn(Optional.of(originalTool));

        final ToolDetailsDTO result = toolService.validateAndEditTool(editedTool);

        Mockito.verify(toolStateRepositoryMock, Mockito.times(1)).save(Mockito.any(ToolState.class));
        Mockito.verify(toolRepositoryMock, Mockito.times(1)).save(Mockito.any(Tool.class));
        assertTool(originalTool, Tool.from(result, ToolState.from(result.toolState())));
    }

    private void assertTool(final Tool originalTool, final Tool resultTool) {
        assertEquals(originalTool.getName(), resultTool.getName());
        assertEquals(originalTool.getDescription(), resultTool.getDescription());
        assertEquals(originalTool.getToolCategory(), resultTool.getToolCategory());
        assertEquals(originalTool.getPrice(), resultTool.getPrice());
        assertEquals(originalTool.getToolState().getStateType(), resultTool.getToolState().getStateType());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistWhileEditingTool() {
        final User worker = UserCreatorHelper.I.createWorker();
        final ToolDetailsDTO expected = ToolCreatorHelper.I.createToolDetailsDTO();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(worker);

        assertThrows(UserNotFoundException.class, () -> toolService.validateAndEditTool(expected), ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAllowedToEditTool() {
        final User worker = UserCreatorHelper.I.createCustomer();
        final ToolDetailsDTO expected = ToolCreatorHelper.I.createToolDetailsDTO();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(worker);
        Mockito.when(userServiceMock.getUserByEmail(worker.getEmail())).thenReturn(Optional.of(worker));

        assertThrows(NoAccessForUserRoleException.class, () -> toolService.validateAndEditTool(expected), USER_NOT_ALLOWED_ERROR_MESSAGE);
    }

    @Test
    void shouldDeleteTool() {
        final User worker = UserCreatorHelper.I.createWorker();
        final Tool tool = ToolCreatorHelper.I.createTool();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(worker);
        Mockito.when(userServiceMock.getUserByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        Mockito.when(toolRepositoryMock.findById(1L)).thenReturn(Optional.of(tool));

        final boolean result = toolService.deleteTool(1L);

        assertTrue(result);
        assertTrue(tool.isRemoved());
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNullWhileDeletingTools() {
        assertThrows(UserUnauthenticatedException.class, () -> toolService.deleteTool(1L), UNAUTHENTICATED_ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistWhileDeletingTool() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);

        assertThrows(UserNotFoundException.class, () -> toolService.deleteTool(1L), ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAllowedToDeleteTool() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(userServiceMock.getUserByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        assertThrows(NoAccessForUserRoleException.class, () -> toolService.deleteTool(1L), USER_NOT_ALLOWED_ERROR_MESSAGE);
    }

    @Test
    void shouldGetToolsByName() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final List<Tool> tools = ToolCreatorHelper.I.createCommonNameToolList().stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()))
            .toList();

        Mockito.when(toolRepositoryMock.findByNameContainingIgnoreCase(toolSearchDTO.toolName())).thenReturn(tools);

        final List<ToolDetailsDTO> expectedTools = ToolCreatorHelper.I.createCommonNameToolDTOList();
        final List<ToolDetailsDTO> result = toolService.getToolsByName(toolSearchDTO);

        assertEquals(2, result.size());
        assertTools(expectedTools, result);
    }

    @Test
    void shouldGetToolDetailsByReservationHasTool() {
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final Tool tool = ToolCreatorHelper.I.createTool();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();

        Mockito.when(toolRepositoryMock.findAllByReservationHasToolsIn(reservation.getReservationHasTools())).thenReturn(List.of(tool));

        final List<ToolDetailsDTO> result = toolService.getToolDetailsByReservationHasTools(reservation.getReservationHasTools());

        assertEquals(1, result.size());
        assertToolDetailsDTO(toolDetailsDTO, result.get(0));
    }

    @Test
    void shouldNotGetToolDetailsByReservationHasTool() {
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();

        Mockito.when(toolRepositoryMock.findAllByReservationHasToolsIn(reservation.getReservationHasTools())).thenReturn(List.of());

        final List<ToolDetailsDTO> result = toolService.getToolDetailsByReservationHasTools(reservation.getReservationHasTools());

        assertEquals(0, result.size());
    }

    @Test
    void shouldNotThrowExceptionWhenToolIsAvailable() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenReturn(Optional.of(true));

        toolService.isToolAvailable(1L);
    }

    @Test
    void shouldNotThrowExceptionWhenToolNotFoundWhileCheckingAvailability() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenThrow(ToolNotFoundException.create(1L));

        assertThrows(ToolNotFoundException.class, () -> toolService.isToolAvailable(1L), "Tool#1 could not be found!");
    }

    @Test
    void shouldNotThrowExceptionWhenToolIsNotAvailable() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenReturn(Optional.of(false));

        assertThrows(ToolUnavailableException.class, () -> toolService.isToolAvailable(1L), "Tool#1 is unavailable!");
    }

    @Test
    void shouldMakeToolUnavailableAndSave() {
        final Tool tool = ToolCreatorHelper.I.createTool();

        toolService.makeToolUnavailableAndSave(tool);

        assertFalse(tool.isAvailable());
    }

    @Test
    void shouldGetToolsByReservationHasTools() {
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();
        final Reservation reservation = ReservationCreatorHelper.I.createReservation();
        final List<ReservationHasTool> reservationHasTools = List.of(new ReservationHasTool(tools.get(0), reservation),
            new ReservationHasTool(tools.get(1), reservation), new ReservationHasTool(tools.get(2), reservation));

        Mockito.when(toolRepositoryMock.findAllByReservationHasToolsIn(reservationHasTools)).thenReturn(tools);

        final List<Tool> result = toolService.getToolsByReservationHasTools(reservationHasTools);

        assertEquals(3, result.size());
        assertEquals("Hammer", result.get(0).getName());
        assertEquals("Loader", result.get(1).getName());
        assertEquals("Crane", result.get(2).getName());
    }

    @Test
    void shouldMakeToolAvailableAndSave() {
        final Tool unavailableTool = ToolCreatorHelper.I.createUnavailableTool();

        toolService.makeToolAvailableAndSave(unavailableTool);

        assertTrue(unavailableTool.isAvailable());
    }

    @Test
    void shouldGetAllTools() {
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();
        final User worker = UserCreatorHelper.I.createWorker();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(worker);
        Mockito.when(userServiceMock.getUserByEmail(worker.getEmail())).thenReturn(Optional.of(worker));
        Mockito.when(toolRepositoryMock.findAll()).thenReturn(tools);

        final ToolListingDTO result = toolService.getAllTools();

        assertEquals(3, result.count());
        assertEquals(3, result.tools().size());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistGetAllTools() {
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();

        Mockito.when(toolRepositoryMock.findAll()).thenReturn(tools);

        assertThrows(UserUnauthenticatedException.class, () -> toolService.getAllTools(), UNAUTHENTICATED_ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAllowedToGetAllTools() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(userServiceMock.getUserByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        assertThrows(NoAccessForUserRoleException.class, () -> toolService.getAllTools(), USER_NOT_ALLOWED_ERROR_MESSAGE);
    }

    private void assertTools(final List<ToolDetailsDTO> expectedTools, final List<ToolDetailsDTO> resultTools) {
        resultTools.forEach(resultTool -> {
            final Optional<ToolDetailsDTO> expected = expectedTools.stream()
                .filter(expectedTool -> expectedTool.name().equals(resultTool.name()))
                .findFirst();

            if (expected.isEmpty()) {
                throw new IllegalStateException("Expected tool cannot be empty!");
            }

            assertToolDetailsDTO(expected.get(), resultTool);
        });
    }

    private void assertToolDTO(final ToolDTO expected, final ToolDetailsDTO resultTool) {
        assertEquals(expected.name(), resultTool.name());
        assertEquals(expected.description(), resultTool.description());
        assertEquals(expected.toolCategory(), resultTool.toolCategory());
        assertEquals(expected.price(), resultTool.price());
        assertEquals(expected.toolState().stateType(), resultTool.toolState().stateType());
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