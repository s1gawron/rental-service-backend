package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.AddToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.validator.ToolDTOValidator;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import com.s1gawron.rentalservice.tool.repository.ToolStateRepository;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ToolService {

    private static final String ELEMENT_NAME = "TOOL MANAGEMENT";

    private final ToolRepository toolRepository;

    private final ToolStateRepository toolStateRepository;

    private final UserService userService;

    @Transactional(readOnly = true)
    public ToolListingDTO getToolsByCategory(final String category) {
        final ToolCategory toolCategory = ToolCategory.findByValue(category);
        final List<ToolDTO> toolDTOSByCategory = toolRepository.findAllByToolCategory(toolCategory)
            .stream()
            .map(Tool::toToolDTO)
            .collect(Collectors.toList());

        return ToolListingDTO.create(toolDTOSByCategory);
    }

    @Transactional(readOnly = true)
    public List<ToolDTO> getNewTools() {
        return toolRepository.findNewTools()
            .stream()
            .map(Tool::toToolDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ToolDTO getToolDetails(final Long toolId) {
        final Tool tool = getToolById(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));
        return tool.toToolDTO();
    }

    @Transactional(readOnly = true)
    public Optional<Tool> getToolById(final Long toolId) {
        return toolRepository.findById(toolId);
    }

    @Transactional(readOnly = true)
    public List<ToolDTO> getToolsByName(final String toolName) {
        return toolRepository.findByNameContainingIgnoreCase(toolName).stream()
            .map(Tool::toToolDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public ToolDTO validateAndAddTool(final AddToolDTO addToolDTO) {
        canUserPerformActionOnTools();
        ToolDTOValidator.I.validate(addToolDTO);

        final ToolState toolState = ToolState.from(addToolDTO.getToolState());
        final Tool tool = Tool.from(addToolDTO, toolState);

        toolStateRepository.save(toolState);
        toolRepository.save(tool);

        return tool.toToolDTO();
    }

    @Transactional
    public ToolDTO validateAndEditTool(final ToolDTO toolDTO) {
        canUserPerformActionOnTools();
        ToolDTOValidator.I.validate(toolDTO);

        final Tool tool = getToolById(toolDTO.getToolId()).orElseThrow(() -> ToolNotFoundException.create(toolDTO.getToolId()));
        final ToolState toolState = tool.getToolState();

        toolState.edit(toolDTO.getToolState());
        toolStateRepository.save(toolState);
        tool.edit(toolDTO);
        toolRepository.save(tool);

        return tool.toToolDTO();
    }

    @Transactional
    public boolean deleteTool(final Long toolId) {
        canUserPerformActionOnTools();

        final Tool tool = getToolById(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));
        final ToolState toolState = tool.getToolState();

        toolStateRepository.delete(toolState);
        toolRepository.delete(tool);

        return true;
    }

    private void canUserPerformActionOnTools() {
        final String authenticatedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final User user = userService.getUserByEmail(authenticatedUserEmail).orElseThrow(() -> UserNotFoundException.create(authenticatedUserEmail));

        if (user.getUserRole() != UserRole.WORKER) {
            throw NoAccessForUserRoleException.create(ELEMENT_NAME);
        }
    }
}
