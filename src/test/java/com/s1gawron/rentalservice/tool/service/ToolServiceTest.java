package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.reservation.helper.ReservationCreatorHelper;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolRemovedException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.repository.ToolStateRepository;
import com.s1gawron.rentalservice.user.helper.UserCreatorHelper;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ToolServiceTest {

    private Authentication authenticationMock;

    private ToolRepository toolRepositoryMock;

    private ToolStateRepository toolStateRepositoryMock;

    private ToolService toolService;

    @BeforeEach
    void setUp() {
        authenticationMock = Mockito.mock(Authentication.class);
        final SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        SecurityContextHolder.setContext(securityContextMock);

        toolRepositoryMock = Mockito.mock(ToolRepository.class);
        toolStateRepositoryMock = Mockito.mock(ToolStateRepository.class);
        toolService = new ToolService(toolRepositoryMock, toolStateRepositoryMock);
    }

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenPrincipalIsNull() {
        final List<Tool> heavyTools = ToolCreatorHelper.I.createToolList().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .toList();

        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.HEAVY.name(), false)).thenReturn(heavyTools);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.HEAVY);

        assertEquals(2, result.count());
        assertEquals(2, result.tools().size());
        result.tools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY.name(), toolDTO.toolCategory()));
    }

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenPrincipalIsAnonymousUser() {
        final List<Tool> heavyTools = ToolCreatorHelper.I.createToolList().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .toList();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn("anonymousUser");
        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.HEAVY.name(), false)).thenReturn(heavyTools);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.HEAVY);

        assertEquals(2, result.count());
        assertEquals(2, result.tools().size());
        result.tools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY.name(), toolDTO.toolCategory()));
    }

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenPrincipalIsWorker() {
        final List<Tool> mixedHeavyTools = Stream.concat(ToolCreatorHelper.I.createToolList().stream(), ToolCreatorHelper.I.createRemovedTools().stream())
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .toList();

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.HEAVY.name())).thenReturn(mixedHeavyTools);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.HEAVY);

        assertEquals(5, result.count());
        assertEquals(5, result.tools().size());
        result.tools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY.name(), toolDTO.toolCategory()));
    }

    @Test
    void shouldNotGetHeavyToolsByLightCategory() {
        final List<Tool> lightTools = ToolCreatorHelper.I.createHeavyTools().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.LIGHT))
            .toList();

        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.LIGHT.name())).thenReturn(lightTools);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.LIGHT);

        assertEquals(0, result.count());
        assertEquals(0, result.tools().size());
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
        final ToolDTO expected = ToolCreatorHelper.I.createToolDTO();

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();

        final ToolDetailsDTO result = toolService.validateAndAddTool(expected);

        Mockito.verify(toolStateRepositoryMock, Mockito.times(1)).save(Mockito.any(ToolState.class));
        Mockito.verify(toolRepositoryMock, Mockito.times(1)).save(Mockito.any(Tool.class));
        assertToolDTO(expected, result);
    }

    @Test
    void shouldValidateAndEditTool() {
        final Tool originalTool = ToolCreatorHelper.I.createTool();
        final ToolDetailsDTO editedTool = ToolCreatorHelper.I.createEditedToolDTO();

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
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
    void shouldDeleteTool() {
        final Tool tool = ToolCreatorHelper.I.createTool();

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolRepositoryMock.findById(1L)).thenReturn(Optional.of(tool));

        final boolean result = toolService.deleteTool(1L);

        assertTrue(result);
        assertTrue(tool.isRemoved());
        assertFalse(tool.isAvailable());
    }

    @Test
    void shouldGetNotRemovedToolsByNameWhenUserIsUnauthenticated() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final List<Tool> tools = ToolCreatorHelper.I.createCommonNameToolList(false).stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()))
            .toList();

        Mockito.when(toolRepositoryMock.findNotRemovedToolsByName(toolSearchDTO.toolName())).thenReturn(tools);

        final List<ToolDetailsDTO> result = toolService.getToolsByName(toolSearchDTO);

        assertEquals(2, result.size());
    }

    @Test
    void shouldGetNotRemovedToolsByNameWhenUserIsCustomer() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final List<Tool> tools = ToolCreatorHelper.I.createCommonNameToolList(false).stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()))
            .toList();
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(toolRepositoryMock.findNotRemovedToolsByName(toolSearchDTO.toolName())).thenReturn(tools);

        final List<ToolDetailsDTO> result = toolService.getToolsByName(toolSearchDTO);

        assertEquals(2, result.size());
    }

    @Test
    void shouldGetAllToolsByNameWhenUserIsWorker() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final Stream<Tool> tools = ToolCreatorHelper.I.createCommonNameToolList(false).stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()));
        final Stream<Tool> removedTools = ToolCreatorHelper.I.createCommonNameToolList(true).stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()));

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolRepositoryMock.findByName(toolSearchDTO.toolName())).thenReturn(Stream.concat(tools, removedTools).toList());

        final List<ToolDetailsDTO> result = toolService.getToolsByName(toolSearchDTO);

        assertEquals(4, result.size());
    }

    @Test
    void shouldGetEmptyListWhenNoToolAreFoundByName() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final List<ToolDetailsDTO> result = toolService.getToolsByName(toolSearchDTO);

        assertEquals(0, result.size());
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
    void shouldNotThrowExceptionWhenToolIsAvailableAndNotRemoved() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenReturn(Optional.of(true));
        Mockito.when(toolRepositoryMock.isToolRemoved(1L)).thenReturn(Optional.of(false));

        toolService.isToolAvailableOrRemoved(1L);
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotFoundWhileCheckingAvailability() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenThrow(ToolNotFoundException.create(1L));

        assertThrows(ToolNotFoundException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotAvailable() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenReturn(Optional.of(false));

        assertThrows(ToolUnavailableException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 is unavailable!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotFoundWhileCheckingRemoveStatus() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenReturn(Optional.of(true));
        Mockito.when(toolRepositoryMock.isToolRemoved(1L)).thenThrow(ToolNotFoundException.create(1L));

        assertThrows(ToolNotFoundException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsRemoved() {
        Mockito.when(toolRepositoryMock.isToolAvailable(1L)).thenReturn(Optional.of(true));
        Mockito.when(toolRepositoryMock.isToolRemoved(1L)).thenReturn(Optional.of(true));

        assertThrows(ToolRemovedException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 is removed!");
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
    void shouldReturnNotRemovedToolsForUnauthenticatedUser() {
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();

        Mockito.when(toolRepositoryMock.findAll(false)).thenReturn(tools);

        final ToolListingDTO result = toolService.getAllTools();

        assertEquals(3, result.count());
        assertEquals(3, result.tools().size());
    }

    @Test
    void shouldReturnNotRemovedToolsForCustomer() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(toolRepositoryMock.findAll(false)).thenReturn(tools);

        final ToolListingDTO result = toolService.getAllTools();

        assertEquals(3, result.count());
        assertEquals(3, result.tools().size());
    }

    @Test
    void shouldReturnAllToolsForWorker() {
        final List<Tool> mixedTools = Stream.concat(ToolCreatorHelper.I.createToolList().stream(), ToolCreatorHelper.I.createRemovedTools().stream()).toList();

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolRepositoryMock.findAllWithLimit()).thenReturn(mixedTools);

        final ToolListingDTO result = toolService.getAllTools();

        assertEquals(9, result.count());
        assertEquals(9, result.tools().size());
        assertEquals(6, result.tools().stream().filter(ToolDetailsDTO::removed).count());
        assertEquals(3, result.tools().stream().filter(tool -> !tool.removed()).count());
    }

    private void assertTools(final List<ToolDetailsDTO> expectedTools, final List<ToolDetailsDTO> resultTools) {
        assertEquals(expectedTools.size(), resultTools.size());
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