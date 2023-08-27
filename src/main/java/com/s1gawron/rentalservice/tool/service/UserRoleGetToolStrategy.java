package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public enum UserRoleGetToolStrategy {

    ANONYMOUS {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory, final Pageable pageable) {
            return getToolsByCategoryForAnonymousOrCustomer(toolDAO, toolCategory, pageable);
        }

        @Override public ToolListingDTO getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO, final Pageable pageable) {
            return getToolsByNameForAnonymousOrCustomer(toolDAO, toolSearchDTO, pageable);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO, final Pageable pageable) {
            return getAllToolsForAnonymousOrCustomer(toolDAO, pageable);
        }
    },
    CUSTOMER {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory, final Pageable pageable) {
            return getToolsByCategoryForAnonymousOrCustomer(toolDAO, toolCategory, pageable);
        }

        @Override public ToolListingDTO getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO, final Pageable pageable) {
            return getToolsByNameForAnonymousOrCustomer(toolDAO, toolSearchDTO, pageable);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO, final Pageable pageable) {
            return getAllToolsForAnonymousOrCustomer(toolDAO, pageable);
        }
    },
    WORKER {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory, final Pageable pageable) {
            return getToolsByCategoryForWorkerOrAdmin(toolDAO, toolCategory, pageable);
        }

        @Override public ToolListingDTO getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO, final Pageable pageable) {
            return getToolsByNameForWorkerOrAdmin(toolDAO, toolSearchDTO, pageable);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO, final Pageable pageable) {
            return getAllToolsForWorkerOrAdmin(toolDAO, pageable);
        }
    },
    ADMIN {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory, final Pageable pageable) {
            return getToolsByCategoryForWorkerOrAdmin(toolDAO, toolCategory, pageable);
        }

        @Override public ToolListingDTO getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO, final Pageable pageable) {
            return getToolsByNameForWorkerOrAdmin(toolDAO, toolSearchDTO, pageable);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO, final Pageable pageable) {
            return getAllToolsForWorkerOrAdmin(toolDAO, pageable);
        }
    };

    protected ToolListingDTO getToolsByCategoryForAnonymousOrCustomer(final ToolDAO toolDAO, final ToolCategory toolCategory, final Pageable pageable) {
        final Page<Tool> allByToolCategory = toolDAO.findAllByToolCategory(toolCategory, false, pageable);
        return toToolListingDTO(allByToolCategory);
    }

    protected ToolListingDTO getToolsByNameForAnonymousOrCustomer(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO, final Pageable pageable) {
        final Page<Tool> notRemovedToolsByName = toolDAO.findNotRemovedToolsByName(toolSearchDTO.toolName(), pageable);
        return toToolListingDTO(notRemovedToolsByName);
    }

    protected ToolListingDTO getAllToolsForAnonymousOrCustomer(final ToolDAO toolDAO, final Pageable pageable) {
        final Page<Tool> allTools = toolDAO.findAll(false, pageable);
        return toToolListingDTO(allTools);
    }

    protected ToolListingDTO getToolsByCategoryForWorkerOrAdmin(final ToolDAO toolDAO, final ToolCategory toolCategory, final Pageable pageable) {
        final Page<Tool> allByToolCategory = toolDAO.findAllByToolCategory(toolCategory, pageable);
        return toToolListingDTO(allByToolCategory);
    }

    protected ToolListingDTO getToolsByNameForWorkerOrAdmin(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO, final Pageable pageable) {
        final Page<Tool> allToolsByName = toolDAO.findByName(toolSearchDTO.toolName(), pageable);
        return toToolListingDTO(allToolsByName);
    }

    protected ToolListingDTO getAllToolsForWorkerOrAdmin(final ToolDAO toolDAO, final Pageable pageable) {
        final Page<Tool> allTools = toolDAO.findAllWithLimit(pageable);
        return toToolListingDTO(allTools);
    }

    private ToolListingDTO toToolListingDTO(final Page<Tool> toolPage) {
        final List<ToolDetailsDTO> toolList = toolPage.map(Tool::toToolDetailsDTO).toList();
        return new ToolListingDTO(toolPage.getTotalPages(), (int) toolPage.getTotalElements(), toolList);
    }

    private static final Map<UserRole, UserRoleGetToolStrategy> STRATEGY_MAP = Map.of(UserRole.ANONYMOUS, UserRoleGetToolStrategy.ANONYMOUS,
        UserRole.CUSTOMER, UserRoleGetToolStrategy.CUSTOMER,
        UserRole.WORKER, UserRoleGetToolStrategy.WORKER,
        UserRole.ADMIN, UserRoleGetToolStrategy.ADMIN);

    public abstract ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory, final Pageable pageable);

    public abstract ToolListingDTO getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO, final Pageable pageable);

    public abstract ToolListingDTO getAllTools(final ToolDAO toolDAO, final Pageable pageable);

    public static UserRoleGetToolStrategy of(final UserRole userRole) {
        return STRATEGY_MAP.getOrDefault(userRole, UserRoleGetToolStrategy.ANONYMOUS);
    }

}
