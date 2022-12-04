package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.AddToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.repository.ToolStateRepository;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ToolServiceTest {

    private static final String USER_EMAIL = "test@test.pl";

    private SecurityContext securityContextMock;

    private ToolRepository toolRepositoryMock;

    private ToolStateRepository toolStateRepositoryMock;

    private UserService userServiceMock;

    private ToolService toolService;

    @BeforeEach
    void setUp() {
        final Authentication authentication = Mockito.mock(Authentication.class);
        securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(USER_EMAIL);
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
            .collect(Collectors.toList());

        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.HEAVY)).thenReturn(heavyTools);

        final ToolListingDTO result = toolService.getToolsByCategory("heavy");

        assertEquals(2, result.getCount());
        assertEquals(2, result.getTools().size());
        result.getTools().forEach(toolDTO -> assertEquals(ToolCategory.HEAVY, toolDTO.getToolCategory()));
    }

    @Test
    void shouldNotGetHeavyToolsByLightCategory() {
        final List<Tool> lightTools = ToolCreatorHelper.I.createHeavyTools().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.LIGHT))
            .collect(Collectors.toList());

        Mockito.when(toolRepositoryMock.findAllByToolCategory(ToolCategory.LIGHT)).thenReturn(lightTools);

        final ToolListingDTO result = toolService.getToolsByCategory("light");

        assertEquals(0, result.getCount());
        assertEquals(0, result.getTools().size());
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

        final List<ToolDTO> expectedTools = ToolCreatorHelper.I.createToolDTOList();
        final List<ToolDTO> result = toolService.getNewTools();

        assertEquals(3, result.size());
        assertTools(expectedTools, result);
    }

    @Test
    void shouldGetToolById() {
        final Tool tool = ToolCreatorHelper.I.createTool();

        Mockito.when(toolRepositoryMock.findById(1L)).thenReturn(Optional.of(tool));

        final ToolDTO expected = ToolDTO.from(tool);
        final ToolDTO result = toolService.getToolById(1L);

        assertToolDTO(expected, result);
    }

    @Test
    void shouldThrowExceptionWhenToolIsEmpty() {
        assertThrows(ToolNotFoundException.class, () -> toolService.getToolById(1L), "Tool: 1 could not be found!");
    }

    @Test
    void shouldValidateAndAddTool() {
        final User user = createUser(UserRole.WORKER);
        final AddToolDTO expected = ToolCreatorHelper.I.createAddToolDTO();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        final ToolDTO result = toolService.validateAndAddTool(expected);

        Mockito.verify(toolStateRepositoryMock, Mockito.times(1)).save(Mockito.any(ToolState.class));
        Mockito.verify(toolRepositoryMock, Mockito.times(1)).save(Mockito.any(Tool.class));
        assertAddToolDTO(expected, result);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistWhileAddingTool() {
        final AddToolDTO expected = ToolCreatorHelper.I.createAddToolDTO();

        assertThrows(UserNotFoundException.class, () -> toolService.validateAndAddTool(expected),
            "User: " + USER_EMAIL + " could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAllowedToAddTool() {
        final User user = createUser(UserRole.CUSTOMER);
        final AddToolDTO expected = ToolCreatorHelper.I.createAddToolDTO();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(NoAccessForUserRoleException.class, () -> toolService.validateAndAddTool(expected),
            "Current user role is not allowed to use: TOOL MANAGEMENT module!");
    }

    @Test
    void shouldValidateAndEditTool() {
        final User user = createUser(UserRole.WORKER);
        final Tool originalTool = ToolCreatorHelper.I.createTool();
        final ToolDTO editedTool = ToolCreatorHelper.I.createEditedToolDTO();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(toolRepositoryMock.findById(1L)).thenReturn(Optional.of(originalTool));

        final ToolDTO result = toolService.validateAndEditTool(editedTool);

        Mockito.verify(toolStateRepositoryMock, Mockito.times(1)).save(Mockito.any(ToolState.class));
        Mockito.verify(toolRepositoryMock, Mockito.times(1)).save(Mockito.any(Tool.class));
        assertTool(originalTool, Tool.from(result, ToolState.from(result.getToolState())));
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
        final ToolDTO expected = ToolCreatorHelper.I.createToolDTO();

        assertThrows(UserNotFoundException.class, () -> toolService.validateAndEditTool(expected),
            "User: " + USER_EMAIL + " could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAllowedToEditTool() {
        final User user = createUser(UserRole.CUSTOMER);
        final ToolDTO expected = ToolCreatorHelper.I.createToolDTO();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(NoAccessForUserRoleException.class, () -> toolService.validateAndEditTool(expected),
            "Current user role is not allowed to use: TOOL MANAGEMENT module!");
    }

    @Test
    void shouldDeleteTool() {
        final User user = createUser(UserRole.WORKER);
        final Tool tool = ToolCreatorHelper.I.createTool();

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(toolRepositoryMock.findById(1L)).thenReturn(Optional.of(tool));

        final boolean result = toolService.deleteTool(1L);

        Mockito.verify(toolStateRepositoryMock, Mockito.times(1)).delete(Mockito.any(ToolState.class));
        Mockito.verify(toolRepositoryMock, Mockito.times(1)).delete(Mockito.any(Tool.class));
        assertTrue(result);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistWhileDeletingTool() {
        assertThrows(UserNotFoundException.class, () -> toolService.deleteTool(1L),
            "User: " + USER_EMAIL + " could not be found!");
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAllowedToDeleteTool() {
        final User user = createUser(UserRole.CUSTOMER);

        Mockito.when(userServiceMock.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(NoAccessForUserRoleException.class, () -> toolService.deleteTool(1L),
            "Current user role is not allowed to use: TOOL MANAGEMENT module!");
    }

    private void assertTools(final List<ToolDTO> expectedTools, final List<ToolDTO> resultTools) {
        resultTools.forEach(resultTool -> {
            final Optional<ToolDTO> expected = expectedTools.stream()
                .filter(expectedTool -> expectedTool.getName().equals(resultTool.getName()))
                .findFirst();

            if (expected.isEmpty()) {
                throw new IllegalStateException("Expected tool cannot be empty!");
            }

            assertToolDTO(expected.get(), resultTool);
        });
    }

    private void assertAddToolDTO(final AddToolDTO expected, final ToolDTO resultTool) {
        assertEquals(expected.getName(), resultTool.getName());
        assertEquals(expected.getDescription(), resultTool.getDescription());
        assertEquals(expected.getToolCategory(), resultTool.getToolCategory());
        assertEquals(expected.getPrice(), resultTool.getPrice());
        assertEquals(expected.getToolState().getStateType(), resultTool.getToolState().getStateType());
    }

    private void assertToolDTO(final ToolDTO expected, final ToolDTO resultTool) {
        assertEquals(expected.getName(), resultTool.getName());
        assertEquals(expected.getDescription(), resultTool.getDescription());
        assertEquals(expected.getToolCategory(), resultTool.getToolCategory());
        assertEquals(expected.getPrice(), resultTool.getPrice());
        assertEquals(expected.getToolState().getStateType(), resultTool.getToolState().getStateType());
    }

    private User createUser(final UserRole userRole) {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(USER_EMAIL, "Start00!", "John", "Kowalski", userRole.getName(), addressDTO);
        return User.createUser(userRegisterDTO, userRole, "encryptedPassword");
    }

}