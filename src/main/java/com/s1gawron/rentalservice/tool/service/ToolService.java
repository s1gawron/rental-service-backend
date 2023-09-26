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
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ToolService {

    private final ToolDAO toolDAO;

    public ToolService(final ToolDAO toolDAO) {
        this.toolDAO = toolDAO;
    }

    @Transactional(readOnly = true)
    public ToolListingDTO getToolsByCategory(final ToolCategory toolCategory, final Pageable pageable) {
        final UserRole currentUserRole = UserContextProvider.I.getCurrentUserRoles().stream().findFirst().orElse(UserRole.ANONYMOUS);
        return UserRoleGetToolStrategy.of(currentUserRole).getToolsByCategory(toolDAO, toolCategory, pageable);
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
    public ToolListingDTO getToolsByName(final ToolSearchDTO toolSearchDTO, final Pageable pageable) {
        final UserRole currentUserRole = UserContextProvider.I.getCurrentUserRoles().stream().findFirst().orElse(UserRole.ANONYMOUS);
        return UserRoleGetToolStrategy.of(currentUserRole).getToolsByName(toolDAO, toolSearchDTO, pageable);
    }

    @Transactional
    public ToolDetailsDTO validateAndAddTool(final ToolDTO toolDTO) {
        ToolDTOValidator.I.validate(toolDTO);

        final Tool tool = Tool.from(toolDTO);
        toolDAO.save(tool);

        return tool.toToolDetailsDTO();
    }

    @Transactional
    public ToolDetailsDTO validateAndEditTool(final ToolDetailsDTO toolDetailsDTO) {
        ToolDTOValidator.I.validate(toolDetailsDTO);

        final Tool tool = getToolById(toolDetailsDTO.toolId());
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
    public ToolListingDTO getAllTools(final Pageable pageable) {
        final UserRole currentUserRole = UserContextProvider.I.getCurrentUserRoles().stream().findFirst().orElse(UserRole.ANONYMOUS);
        return UserRoleGetToolStrategy.of(currentUserRole).getAllTools(toolDAO, pageable);
    }

}
