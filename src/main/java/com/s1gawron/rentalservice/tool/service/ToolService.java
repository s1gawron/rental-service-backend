package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
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
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import com.s1gawron.rentalservice.tool.repository.ToolStateDAO;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ToolService {

    private final ToolDAO toolDAO;

    private final ToolStateDAO toolStateDAO;

    public ToolService(final ToolDAO toolDAO, final ToolStateDAO toolStateDAO) {
        this.toolDAO = toolDAO;
        this.toolStateDAO = toolStateDAO;
    }

    @Transactional(readOnly = true)
    public ToolListingDTO getToolsByCategory(final ToolCategory toolCategory) {
        if (isUserCustomerOrUnauthenticated()) {
            final List<Tool> notRemovedToolsByCategory = toolDAO.findAllByToolCategory(toolCategory.name(), false);
            return toToolListingDTO(notRemovedToolsByCategory);
        }

        return toToolListingDTO(toolDAO.findAllByToolCategory(toolCategory.name()));
    }

    @Transactional(readOnly = true)
    public List<ToolDetailsDTO> getNewTools() {
        return toolDAO.findNewTools()
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
        return toolDAO.findById(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));
    }

    @Transactional(readOnly = true)
    public List<ToolDetailsDTO> getToolsByName(final ToolSearchDTO toolSearchDTO) {
        if (isUserCustomerOrUnauthenticated()) {
            return toolDAO.findNotRemovedToolsByName(toolSearchDTO.toolName()).stream()
                .map(Tool::toToolDetailsDTO)
                .toList();
        }

        return toolDAO.findByName(toolSearchDTO.toolName()).stream()
            .map(Tool::toToolDetailsDTO)
            .toList();
    }

    @Transactional
    public ToolDetailsDTO validateAndAddTool(final ToolDTO toolDTO) {
        ToolDTOValidator.I.validate(toolDTO);

        final ToolState toolState = ToolState.from(toolDTO.toolState());
        final Tool tool = Tool.from(toolDTO, toolState);

        toolStateDAO.save(toolState);
        toolDAO.save(tool);

        return tool.toToolDetailsDTO();
    }

    @Transactional
    public ToolDetailsDTO validateAndEditTool(final ToolDetailsDTO toolDetailsDTO) {
        ToolDTOValidator.I.validate(toolDetailsDTO);

        final Tool tool = getToolById(toolDetailsDTO.toolId());
        final ToolState toolState = tool.getToolState();

        toolState.edit(toolDetailsDTO.toolState());
        toolStateDAO.save(toolState);
        tool.edit(toolDetailsDTO);
        toolDAO.save(tool);

        return tool.toToolDetailsDTO();
    }

    @Transactional
    public boolean deleteTool(final Long toolId) {
        final Tool tool = getToolById(toolId);
        tool.remove();

        toolDAO.save(tool);

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
        final boolean isNotAvailable = !toolDAO.isToolAvailable(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));

        if (isNotAvailable) {
            throw ToolUnavailableException.create(toolId);
        }

        final boolean isRemoved = toolDAO.isToolRemoved(toolId).orElseThrow(() -> ToolNotFoundException.create(toolId));

        if (isRemoved) {
            throw ToolRemovedException.create(toolId);
        }
    }

    @Transactional
    public void makeToolUnavailableAndSave(final Tool tool) {
        tool.makeToolUnavailable();
        toolDAO.save(tool);
    }

    @Transactional(readOnly = true)
    public List<Tool> getToolsByReservationHasTools(final List<ReservationHasTool> reservationHasTools) {
        return toolDAO.findAllByReservationHasToolsIn(reservationHasTools);
    }

    @Transactional
    public void makeToolAvailableAndSave(final Tool tool) {
        tool.makeToolAvailable();
        toolDAO.save(tool);
    }

    @Transactional(readOnly = true)
    public ToolListingDTO getAllTools() {
        if (isUserCustomerOrUnauthenticated()) {
            return toToolListingDTO(toolDAO.findAll(false));
        }

        return toToolListingDTO(toolDAO.findAllWithLimit());
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
