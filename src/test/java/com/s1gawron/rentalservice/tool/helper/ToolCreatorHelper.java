package com.s1gawron.rentalservice.tool.helper;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.model.ToolStateType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public enum ToolCreatorHelper {

    I;

    public List<ToolDetailsDTO> createToolDTOList() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.getName(),
            BigDecimal.valueOf(10.99), newState);
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.getName(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.getName(),
            BigDecimal.valueOf(1000.99), usedState);
        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.getName(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.getName(),
            BigDecimal.valueOf(19999.99), wornState);

        return List.of(newToolDetailsDTO, usedToolDetailsDTO, wornToolDetailsDTO);
    }

    public List<Tool> createToolList() {
        return createToolDTOList().stream()
            .map(toolDTO -> {
                final ToolState toolState = ToolState.from(toolDTO.getToolState());
                return Tool.from(toolDTO, toolState);
            })
            .collect(Collectors.toList());
    }

    public List<Tool> createHeavyTools() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(),
            BigDecimal.valueOf(10.99), newState);
        final Tool newTool = Tool.from(newToolDetailsDTO, ToolState.from(newState));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.getName(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.getName(),
            BigDecimal.valueOf(1000.99), usedState);
        final Tool usedTool = Tool.from(usedToolDetailsDTO, ToolState.from(usedState));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.getName(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.getName(),
            BigDecimal.valueOf(19999.99), wornState);
        final Tool wornTool = Tool.from(wornToolDetailsDTO, ToolState.from(wornState));

        return List.of(newTool, usedTool, wornTool);
    }

    public List<Tool> createLightTools() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.getName(),
            BigDecimal.valueOf(10.99), newState);
        final Tool newTool = Tool.from(newToolDetailsDTO, ToolState.from(newState));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.getName(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, "Screwdriver", "U know for screws :)", ToolCategory.LIGHT.getName(),
            BigDecimal.valueOf(5.99), usedState);
        final Tool usedTool = Tool.from(usedToolDetailsDTO, ToolState.from(usedState));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.getName(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, "Big hammer", "Just a bigger hammer", ToolCategory.LIGHT.getName(),
            BigDecimal.valueOf(15.99), wornState);
        final Tool wornTool = Tool.from(wornToolDetailsDTO, ToolState.from(wornState));

        return List.of(newTool, usedTool, wornTool);
    }

    public Tool createTool() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(),
            BigDecimal.valueOf(10.99), newState);
        return Tool.from(newToolDetailsDTO, ToolState.from(newState));
    }

    public ToolDetailsDTO createToolDetailsDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        return new ToolDetailsDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(10.99), newState);
    }

    public ToolDetailsDTO createEditedToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New");
        return new ToolDetailsDTO(1L, "Hammer#2", "It's a second hammer", ToolCategory.LIGHT.getName(), BigDecimal.valueOf(5.99), newState);
    }

    public ToolDTO createToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        return new ToolDTO("Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(10.99), newState);
    }

    public List<ToolDetailsDTO> createCommonNameToolDTOList() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.getName(),
            BigDecimal.valueOf(10.99), newState);
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.getName(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, "Big hammer", "It's just a bigger hammer :)", ToolCategory.LIGHT.getName(),
            BigDecimal.valueOf(1000.99),
            usedState);
        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.getName(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, "Screwdriver", "Not a hammer", ToolCategory.LIGHT.getName(),
            BigDecimal.valueOf(19999.99), wornState);

        return List.of(newToolDetailsDTO, usedToolDetailsDTO, wornToolDetailsDTO);
    }

    public List<Tool> createCommonNameToolList() {
        return createCommonNameToolDTOList().stream()
            .map(toolDTO -> {
                final ToolState toolState = ToolState.from(toolDTO.getToolState());
                return Tool.from(toolDTO, toolState);
            })
            .collect(Collectors.toList());
    }

}
