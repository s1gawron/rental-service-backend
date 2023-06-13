package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.usercontext.UserContextProvider;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.dto.validator.ToolDTOValidator;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolRemovedException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.repository.ToolStateRepository;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ToolService {

    private static final String ELEMENT_NAME = "TOOL MANAGEMENT";

    private final ToolRepository toolRepository;

    private final ToolStateRepository toolStateRepository;

    public ToolService(final ToolRepository toolRepository, final ToolStateRepository toolStateRepository) {
        this.toolRepository = toolRepository;
        this.toolStateRepository = toolStateRepository;
    }

    @Transactional(readOnly = true)
    public ToolListingDTO getToolsByCategory(final ToolCategory toolCategory) {
        if (isUserCustomerOrUnauthenticated()) {
            final List<Tool> notRemovedToolsByCategory = toolRepository.findAllByToolCategory(toolCategory.name(), false);
            return toToolListingDTO(notRemovedToolsByCategory);
        }

        return toToolListingDTO(toolRepository.findAllByToolCategory(toolCategory.name()));
    }

    @Transactional(readOnly = true)
    public List<ToolDetailsDTO> getNewTools() {
        return toolRepository.findNewTools()
            .stream()
            .map(Tool::toToolDetailsDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public ToolDetailsDTO getToolDetails(final Long toolId) {
        return getToolById(toolId).toToolDetailsDTO();
    }

    @Transactional(readOnly = true)
    public Tool getToolById(final Long toolId) {
        return toolRepository.findById(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));
    }

    @Transactional(readOnly = true)
    public List<ToolDetailsDTO> getToolsByName(final ToolSearchDTO toolSearchDTO) {
        if (isUserCustomerOrUnauthenticated()) {
            return toolRepository.findNotRemovedToolsByName(toolSearchDTO.toolName()).stream()
                .map(Tool::toToolDetailsDTO)
                .toList();
        }

        return toolRepository.findByName(toolSearchDTO.toolName()).stream()
            .map(Tool::toToolDetailsDTO)
            .toList();
    }

    @Transactional
    public ToolDetailsDTO validateAndAddTool(final ToolDTO toolDTO) {
        if (isUserCustomerOrUnauthenticated()) {
            throw NoAccessForUserRoleException.create(ELEMENT_NAME);
        }

        ToolDTOValidator.I.validate(toolDTO);

        final ToolState toolState = ToolState.from(toolDTO.toolState());
        final Tool tool = Tool.from(toolDTO, toolState);

        toolStateRepository.save(toolState);
        toolRepository.save(tool);

        return tool.toToolDetailsDTO();
    }

    @Transactional
    public ToolDetailsDTO validateAndEditTool(final ToolDetailsDTO toolDetailsDTO) {
        if (isUserCustomerOrUnauthenticated()) {
            throw NoAccessForUserRoleException.create(ELEMENT_NAME);
        }

        ToolDTOValidator.I.validate(toolDetailsDTO);

        final Tool tool = getToolById(toolDetailsDTO.toolId());
        final ToolState toolState = tool.getToolState();

        toolState.edit(toolDetailsDTO.toolState());
        toolStateRepository.save(toolState);
        tool.edit(toolDetailsDTO);
        toolRepository.save(tool);

        return tool.toToolDetailsDTO();
    }

    @Transactional
    public boolean deleteTool(final Long toolId) {
        if (isUserCustomerOrUnauthenticated()) {
            throw NoAccessForUserRoleException.create(ELEMENT_NAME);
        }

        final Tool tool = getToolById(toolId);
        tool.remove();

        toolRepository.save(tool);

        return true;
    }

    @Transactional(readOnly = true)
    public List<ToolDetailsDTO> getToolDetailsByReservationHasTools(final List<ReservationHasTool> reservationHasTools) {
        return getToolsByReservationHasTools(reservationHasTools).stream()
            .map(Tool::toToolDetailsDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public void isToolAvailableOrRemoved(final long toolId) {
        final boolean isNotAvailable = !toolRepository.isToolAvailable(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));

        if (isNotAvailable) {
            throw ToolUnavailableException.create(toolId);
        }

        final boolean isRemoved = toolRepository.isToolRemoved(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));

        if (isRemoved) {
            throw ToolRemovedException.create(toolId);
        }
    }

    @Transactional
    public void makeToolUnavailableAndSave(final Tool tool) {
        tool.makeToolUnavailable();
        toolRepository.save(tool);
    }

    @Transactional(readOnly = true)
    public List<Tool> getToolsByReservationHasTools(final List<ReservationHasTool> reservationHasTools) {
        return toolRepository.findAllByReservationHasToolsIn(reservationHasTools);
    }

    @Transactional
    public void makeToolAvailableAndSave(final Tool tool) {
        tool.makeToolAvailable();
        toolRepository.save(tool);
    }

    @Transactional(readOnly = true)
    public ToolListingDTO getAllTools() {
        if (isUserCustomerOrUnauthenticated()) {
            return toToolListingDTO(toolRepository.findAll(false));
        }

        return toToolListingDTO(toolRepository.findAllWithLimit());
    }

    private ToolListingDTO toToolListingDTO(final List<Tool> tools) {
        final List<ToolDetailsDTO> toolDetailsDTOS = tools.stream()
            .map(Tool::toToolDetailsDTO)
            .toList();

        return new ToolListingDTO(toolDetailsDTOS.size(), toolDetailsDTOS);
    }

    private boolean isUserCustomerOrUnauthenticated() {
        final Set<UserRole> userRoles = UserContextProvider.I.getCurrentUserRoles();

        if (userRoles.isEmpty()) {
            return true;
        }

        return userRoles.contains(UserRole.ANONYMOUS) || userRoles.contains(UserRole.CUSTOMER);
    }
}
