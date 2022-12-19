package com.s1gawron.rentalservice.tool.helper;

import com.s1gawron.rentalservice.tool.dto.AddToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
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

    public List<ToolDTO> createToolDTOList() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDTO newToolDTO = new ToolDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.getName(), BigDecimal.valueOf(10.99), newState);
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.getName(), "No signs of usage");
        final ToolDTO usedToolDTO = new ToolDTO(2L, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(1000.99), usedState);
        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.getName(), "Rusty");
        final ToolDTO wornToolDTO = new ToolDTO(3L, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(19999.99), wornState);

        return List.of(newToolDTO, usedToolDTO, wornToolDTO);
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
        final ToolDTO newToolDTO = new ToolDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(10.99), newState);
        final Tool newTool = Tool.from(newToolDTO, ToolState.from(newState));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.getName(), "No signs of usage");
        final ToolDTO usedToolDTO = new ToolDTO(2L, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(1000.99), usedState);
        final Tool usedTool = Tool.from(usedToolDTO, ToolState.from(usedState));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.getName(), "Rusty");
        final ToolDTO wornToolDTO = new ToolDTO(3L, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(19999.99), wornState);
        final Tool wornTool = Tool.from(wornToolDTO, ToolState.from(wornState));

        return List.of(newTool, usedTool, wornTool);
    }

    public Tool createTool() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDTO newToolDTO = new ToolDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(10.99), newState);
        return Tool.from(newToolDTO, ToolState.from(newState));
    }

    public ToolDTO createToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        return new ToolDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(10.99), newState);
    }

    public ToolDTO createEditedToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New");
        return new ToolDTO(1L, "Hammer#2", "It's a second hammer", ToolCategory.LIGHT.getName(), BigDecimal.valueOf(5.99), newState);
    }

    public AddToolDTO createAddToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        return new AddToolDTO("Hammer", "It's just a hammer :)", ToolCategory.HEAVY.getName(), BigDecimal.valueOf(10.99), newState);
    }

    public List<ToolDTO> createCommonNameToolDTOList() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDTO newToolDTO = new ToolDTO(1L, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.getName(), BigDecimal.valueOf(10.99), newState);
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.getName(), "No signs of usage");
        final ToolDTO usedToolDTO = new ToolDTO(2L, "Big hammer", "It's just a bigger hammer :)", ToolCategory.LIGHT.getName(), BigDecimal.valueOf(1000.99),
            usedState);
        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.getName(), "Rusty");
        final ToolDTO wornToolDTO = new ToolDTO(3L, "Screwdriver", "Not a hammer", ToolCategory.LIGHT.getName(), BigDecimal.valueOf(19999.99), wornState);

        return List.of(newToolDTO, usedToolDTO, wornToolDTO);
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
