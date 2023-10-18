package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolRemovedException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import com.s1gawron.rentalservice.shared.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import com.s1gawron.rentalservice.shared.helper.UserCreatorHelper;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ToolServiceTest {

    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 25);

    private Authentication authenticationMock;

    private ToolDAO toolDAOMock;

    private ToolService toolService;

    @BeforeEach
    void setUp() {
        authenticationMock = Mockito.mock(Authentication.class);
        final SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        SecurityContextHolder.setContext(securityContextMock);

        toolDAOMock = Mockito.mock(ToolDAO.class);
        toolService = new ToolService(toolDAOMock);
    }

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenPrincipalIsNull() {
        final List<Tool> heavyTools = ToolCreatorHelper.I.createToolList().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .toList();
        final PageImpl<Tool> toolPage = new PageImpl<>(heavyTools);

        Mockito.when(toolDAOMock.findAllByToolCategory(ToolCategory.HEAVY, false, DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.HEAVY, DEFAULT_PAGEABLE);

        assertEquals(2, result.totalNumberOfTools());
        assertEquals(2, result.tools().size());
        result.tools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY.name(), toolDTO.toolCategory()));
    }

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenPrincipalIsAnonymousUser() {
        final List<Tool> heavyTools = ToolCreatorHelper.I.createToolList().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .toList();
        final PageImpl<Tool> toolPage = new PageImpl<>(heavyTools);

        Mockito.when(authenticationMock.getPrincipal()).thenReturn("anonymousUser");
        Mockito.when(toolDAOMock.findAllByToolCategory(ToolCategory.HEAVY, false, DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.HEAVY, DEFAULT_PAGEABLE);

        assertEquals(2, result.totalNumberOfTools());
        assertEquals(2, result.tools().size());
        result.tools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY.name(), toolDTO.toolCategory()));
    }

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenPrincipalIsWorker() {
        final List<Tool> mixedHeavyTools = Stream.concat(ToolCreatorHelper.I.createToolList().stream(), ToolCreatorHelper.I.createRemovedTools().stream())
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .toList();
        final PageImpl<Tool> toolPage = new PageImpl<>(mixedHeavyTools);

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolDAOMock.findAllByToolCategory(ToolCategory.HEAVY, DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.HEAVY, DEFAULT_PAGEABLE);

        assertEquals(5, result.totalNumberOfTools());
        assertEquals(5, result.tools().size());
        result.tools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY.name(), toolDTO.toolCategory()));
    }

    @Test
    void shouldNotGetHeavyToolsByLightCategory() {
        final List<Tool> lightTools = ToolCreatorHelper.I.createHeavyTools().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.LIGHT))
            .toList();
        final PageImpl<Tool> toolPage = new PageImpl<>(lightTools);

        Mockito.when(toolDAOMock.findAllByToolCategory(ToolCategory.LIGHT, false, DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getToolsByCategory(ToolCategory.LIGHT, DEFAULT_PAGEABLE);

        assertEquals(0, result.totalNumberOfTools());
        assertEquals(0, result.tools().size());
    }

    @Test
    void shouldGetNewTools() {
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();

        Mockito.when(toolDAOMock.findNewTools()).thenReturn(tools);

        final List<ToolDetailsDTO> expectedTools = ToolCreatorHelper.I.createToolDTOList();
        final List<ToolDetailsDTO> result = toolService.getNewTools();

        assertEquals(3, result.size());
        assertTools(expectedTools, result);
    }

    @Test
    void shouldGetToolById() {
        final Tool tool = ToolCreatorHelper.I.createTool();

        Mockito.when(toolDAOMock.findById(1L)).thenReturn(Optional.of(tool));

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

        Mockito.verify(toolDAOMock, Mockito.times(1)).save(Mockito.any(Tool.class));
        assertToolDTO(expected, result);
    }

    @Test
    void shouldValidateAndEditTool() {
        final Tool originalTool = ToolCreatorHelper.I.createTool();
        final ToolDetailsDTO editedTool = ToolCreatorHelper.I.createEditedToolDTO();

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolDAOMock.findById(1L)).thenReturn(Optional.of(originalTool));

        final ToolDetailsDTO result = toolService.validateAndEditTool(editedTool);

        Mockito.verify(toolDAOMock, Mockito.times(1)).save(Mockito.any(Tool.class));
        assertTool(originalTool, Tool.from(result));
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
        Mockito.when(toolDAOMock.findById(1L)).thenReturn(Optional.of(tool));

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
        final PageImpl<Tool> toolPage = new PageImpl<>(tools);

        Mockito.when(toolDAOMock.findNotRemovedToolsByName(toolSearchDTO.toolName(), DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getToolsByName(toolSearchDTO, DEFAULT_PAGEABLE);

        assertEquals(2, result.totalNumberOfTools());
    }

    @Test
    void shouldGetNotRemovedToolsByNameWhenUserIsCustomer() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final List<Tool> tools = ToolCreatorHelper.I.createCommonNameToolList(false).stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()))
            .toList();
        final PageImpl<Tool> toolPage = new PageImpl<>(tools);
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(toolDAOMock.findNotRemovedToolsByName(toolSearchDTO.toolName(), DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getToolsByName(toolSearchDTO, DEFAULT_PAGEABLE);

        assertEquals(2, result.totalNumberOfTools());
    }

    @Test
    void shouldGetAllToolsByNameWhenUserIsWorker() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final Stream<Tool> tools = ToolCreatorHelper.I.createCommonNameToolList(false).stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()));
        final Stream<Tool> removedTools = ToolCreatorHelper.I.createCommonNameToolList(true).stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolSearchDTO.toolName()));
        final List<Tool> mergedTools = Stream.concat(tools, removedTools).toList();
        final PageImpl<Tool> toolPage = new PageImpl<>(mergedTools);

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolDAOMock.findByName(toolSearchDTO.toolName(), DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getToolsByName(toolSearchDTO, DEFAULT_PAGEABLE);

        assertEquals(4, result.totalNumberOfTools());
    }

    @Test
    void shouldGetEmptyListWhenNoToolAreFoundByName() {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");

        Mockito.when(toolDAOMock.findNotRemovedToolsByName(toolSearchDTO.toolName(), DEFAULT_PAGEABLE)).thenReturn(Page.empty());
        final ToolListingDTO result = toolService.getToolsByName(toolSearchDTO, DEFAULT_PAGEABLE);

        assertEquals(0, result.totalNumberOfTools());
    }

    @Test
    void shouldNotThrowExceptionWhenToolIsAvailableAndNotRemoved() {
        Mockito.when(toolDAOMock.isToolAvailable(1L)).thenReturn(Optional.of(true));
        Mockito.when(toolDAOMock.isToolRemoved(1L)).thenReturn(Optional.of(false));

        toolService.isToolAvailableOrRemoved(1L);
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotFoundWhileCheckingAvailability() {
        Mockito.when(toolDAOMock.isToolAvailable(1L)).thenThrow(ToolNotFoundException.create(1L));

        assertThrows(ToolNotFoundException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotAvailable() {
        Mockito.when(toolDAOMock.isToolAvailable(1L)).thenReturn(Optional.of(false));

        assertThrows(ToolUnavailableException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 is unavailable!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsNotFoundWhileCheckingRemoveStatus() {
        Mockito.when(toolDAOMock.isToolAvailable(1L)).thenReturn(Optional.of(true));
        Mockito.when(toolDAOMock.isToolRemoved(1L)).thenThrow(ToolNotFoundException.create(1L));

        assertThrows(ToolNotFoundException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenToolIsRemoved() {
        Mockito.when(toolDAOMock.isToolAvailable(1L)).thenReturn(Optional.of(true));
        Mockito.when(toolDAOMock.isToolRemoved(1L)).thenReturn(Optional.of(true));

        assertThrows(ToolRemovedException.class, () -> toolService.isToolAvailableOrRemoved(1L), "Tool#1 is removed!");
    }

    @Test
    void shouldMakeToolUnavailableAndSave() {
        final Tool tool = ToolCreatorHelper.I.createTool();

        toolService.makeToolUnavailableAndSave(tool);

        assertFalse(tool.isAvailable());
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
        final PageImpl<Tool> toolPage = new PageImpl<>(tools);

        Mockito.when(toolDAOMock.findAll(false, DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getAllTools(DEFAULT_PAGEABLE);

        assertEquals(3, result.totalNumberOfTools());
        assertEquals(3, result.tools().size());
    }

    @Test
    void shouldReturnNotRemovedToolsForCustomer() {
        final User customer = UserCreatorHelper.I.createCustomer();
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();
        final PageImpl<Tool> toolPage = new PageImpl<>(tools);

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(toolDAOMock.findAll(false, DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getAllTools(DEFAULT_PAGEABLE);

        assertEquals(3, result.totalNumberOfTools());
        assertEquals(3, result.tools().size());
    }

    @Test
    void shouldReturnAllToolsForWorker() {
        final List<Tool> mixedTools = Stream.concat(ToolCreatorHelper.I.createToolList().stream(), ToolCreatorHelper.I.createRemovedTools().stream()).toList();
        final PageImpl<Tool> toolPage = new PageImpl<>(mixedTools);

        Mockito.doReturn(List.of(UserRole.WORKER.toSimpleGrantedAuthority())).when(authenticationMock).getAuthorities();
        Mockito.when(toolDAOMock.findAllWithLimit(DEFAULT_PAGEABLE)).thenReturn(toolPage);

        final ToolListingDTO result = toolService.getAllTools(DEFAULT_PAGEABLE);

        assertEquals(9, result.totalNumberOfTools());
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