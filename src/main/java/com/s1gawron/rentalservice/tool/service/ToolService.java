package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.dto.validator.ToolDTOValidator;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.repository.ToolStateRepository;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolService {

    private static final String ELEMENT_NAME = "TOOL MANAGEMENT";

    private final ToolRepository toolRepository;

    private final ToolStateRepository toolStateRepository;

    private final UserService userService;

    public ToolService(final ToolRepository toolRepository, final ToolStateRepository toolStateRepository, final UserService userService) {
        this.toolRepository = toolRepository;
        this.toolStateRepository = toolStateRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public ToolListingDTO getToolsByCategory(final String category) {
        final ToolCategory toolCategory = ToolCategory.findByValue(category);
        final List<ToolDetailsDTO> toolDetailsDTOSByCategory = toolRepository.findAllByToolCategory(toolCategory)
            .stream()
            .map(Tool::toToolDetailsDTO)
            .collect(Collectors.toList());

        return new ToolListingDTO(toolDetailsDTOSByCategory.size(), toolDetailsDTOSByCategory);
    }

    @Transactional(readOnly = true)
    public List<ToolDetailsDTO> getNewTools() {
        return toolRepository.findNewTools()
            .stream()
            .map(Tool::toToolDetailsDTO)
            .collect(Collectors.toList());
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
        final List<ToolDetailsDTO> toolDetails = toolRepository.findByNameContainingIgnoreCase(toolSearchDTO.toolName()).stream()
            .map(Tool::toToolDetailsDTO)
            .collect(Collectors.toList());

        if (toolDetails.isEmpty()) {
            throw ToolNotFoundException.createForName(toolSearchDTO.toolName());
        }

        return toolDetails;
    }

    @Transactional
    public ToolDetailsDTO validateAndAddTool(final ToolDTO toolDTO) {
        canUserPerformActionOnTools();
        ToolDTOValidator.I.validate(toolDTO);

        final ToolState toolState = ToolState.from(toolDTO.toolState());
        final Tool tool = Tool.from(toolDTO, toolState);

        toolStateRepository.save(toolState);
        toolRepository.save(tool);

        return tool.toToolDetailsDTO();
    }

    @Transactional
    public ToolDetailsDTO validateAndEditTool(final ToolDetailsDTO toolDetailsDTO) {
        canUserPerformActionOnTools();
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
        canUserPerformActionOnTools();

        final Tool tool = getToolById(toolId);
        final ToolState toolState = tool.getToolState();

        toolStateRepository.delete(toolState);
        toolRepository.delete(tool);

        return true;
    }

    @Transactional(readOnly = true)
    public List<ToolDetailsDTO> getToolDetailsByReservationHasTools(final List<ReservationHasTool> reservationHasTools) {
        return toolRepository.findAllByReservationHasToolsIn(reservationHasTools).stream()
            .map(Tool::toToolDetailsDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void isToolAvailable(final long toolId) {
        final boolean isAvailable = toolRepository.isToolAvailable(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));

        if (isAvailable) {
            return;
        }

        throw ToolUnavailableException.create(toolId);
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

    private void canUserPerformActionOnTools() {
        final String authenticatedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final User user = userService.getUserByEmail(authenticatedUserEmail).orElseThrow(() -> UserNotFoundException.create(authenticatedUserEmail));

        if (user.isCustomer()) {
            throw NoAccessForUserRoleException.create(ELEMENT_NAME);
        }
    }
}
