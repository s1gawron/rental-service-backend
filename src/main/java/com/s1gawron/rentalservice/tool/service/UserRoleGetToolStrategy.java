package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import com.s1gawron.rentalservice.user.model.UserRole;

import java.util.List;
import java.util.Map;

public enum UserRoleGetToolStrategy {

    ANONYMOUS {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory) {
            return getToolsByCategoryForAnonymousOrCustomer(toolDAO, toolCategory);
        }

        @Override public List<ToolDetailsDTO> getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO) {
            return getToolsByNameForAnonymousOrCustomer(toolDAO, toolSearchDTO);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO) {
            return getAllToolsForAnonymousOrCustomer(toolDAO);
        }
    },
    CUSTOMER {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory) {
            return getToolsByCategoryForAnonymousOrCustomer(toolDAO, toolCategory);
        }

        @Override public List<ToolDetailsDTO> getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO) {
            return getToolsByNameForAnonymousOrCustomer(toolDAO, toolSearchDTO);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO) {
            return getAllToolsForAnonymousOrCustomer(toolDAO);
        }
    },
    WORKER {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory) {
            return getToolsByCategoryForWorkerOrAdmin(toolDAO, toolCategory);
        }

        @Override public List<ToolDetailsDTO> getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO) {
            return getToolsByNameForWorkerOrAdmin(toolDAO, toolSearchDTO);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO) {
            return getAllToolsForWorkerOrAdmin(toolDAO);
        }
    },
    ADMIN {
        @Override public ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory) {
            return getToolsByCategoryForWorkerOrAdmin(toolDAO, toolCategory);
        }

        @Override public List<ToolDetailsDTO> getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO) {
            return getToolsByNameForWorkerOrAdmin(toolDAO, toolSearchDTO);
        }

        @Override public ToolListingDTO getAllTools(final ToolDAO toolDAO) {
            return getAllToolsForWorkerOrAdmin(toolDAO);
        }
    };

    protected ToolListingDTO getToolsByCategoryForAnonymousOrCustomer(final ToolDAO toolDAO, final ToolCategory toolCategory) {
        final List<Tool> allByToolCategory = toolDAO.findAllByToolCategory(toolCategory.name(), false);
        return toToolListingDTO(allByToolCategory);
    }

    protected List<ToolDetailsDTO> getToolsByNameForAnonymousOrCustomer(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO) {
        return toolDAO.findNotRemovedToolsByName(toolSearchDTO.toolName()).stream()
            .map(Tool::toToolDetailsDTO)
            .toList();
    }

    protected ToolListingDTO getAllToolsForAnonymousOrCustomer(final ToolDAO toolDAO) {
        final List<Tool> allTools = toolDAO.findAll(false);
        return toToolListingDTO(allTools);
    }

    protected ToolListingDTO getToolsByCategoryForWorkerOrAdmin(final ToolDAO toolDAO, final ToolCategory toolCategory) {
        final List<Tool> allByToolCategory = toolDAO.findAllByToolCategory(toolCategory.name());
        return toToolListingDTO(allByToolCategory);
    }

    protected List<ToolDetailsDTO> getToolsByNameForWorkerOrAdmin(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO) {
        return toolDAO.findByName(toolSearchDTO.toolName()).stream()
            .map(Tool::toToolDetailsDTO)
            .toList();
    }

    protected ToolListingDTO getAllToolsForWorkerOrAdmin(final ToolDAO toolDAO) {
        final List<Tool> allTools = toolDAO.findAllWithLimit();
        return toToolListingDTO(allTools);
    }

    private ToolListingDTO toToolListingDTO(final List<Tool> tools) {
        final List<ToolDetailsDTO> toolDetailsDTOS = tools.stream()
            .map(Tool::toToolDetailsDTO)
            .toList();

        return new ToolListingDTO(toolDetailsDTOS.size(), toolDetailsDTOS);
    }

    private static final Map<UserRole, UserRoleGetToolStrategy> STRATEGY_MAP = Map.of(UserRole.ANONYMOUS, UserRoleGetToolStrategy.ANONYMOUS,
        UserRole.CUSTOMER, UserRoleGetToolStrategy.CUSTOMER,
        UserRole.WORKER, UserRoleGetToolStrategy.WORKER,
        UserRole.ADMIN, UserRoleGetToolStrategy.ADMIN);

    public abstract ToolListingDTO getToolsByCategory(final ToolDAO toolDAO, final ToolCategory toolCategory);

    public abstract List<ToolDetailsDTO> getToolsByName(final ToolDAO toolDAO, final ToolSearchDTO toolSearchDTO);

    public abstract ToolListingDTO getAllTools(final ToolDAO toolDAO);

    public static UserRoleGetToolStrategy of(final UserRole userRole) {
        return STRATEGY_MAP.getOrDefault(userRole, UserRoleGetToolStrategy.ANONYMOUS);
    }

}
