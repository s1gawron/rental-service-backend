package com.s1gawron.rentalservice.tool.helper;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.model.ToolStateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public enum ToolCreatorHelper {

    I;

    public List<ToolDetailsDTO> createToolDTOList() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");
        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/crane");

        return List.of(newToolDetailsDTO, usedToolDetailsDTO, wornToolDetailsDTO);
    }

    public List<Tool> createToolList() {
        return createToolDTOList().stream()
            .map(toolDTO -> {
                final ToolState toolState = ToolState.from(toolDTO.toolState());
                return Tool.from(toolDTO, toolState);
            })
            .collect(Collectors.toList());
    }

    public List<Tool> createHeavyTools() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO, ToolState.from(newState));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");
        final Tool usedTool = Tool.from(usedToolDetailsDTO, ToolState.from(usedState));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/crane");
        final Tool wornTool = Tool.from(wornToolDetailsDTO, ToolState.from(wornState));

        return List.of(newTool, usedTool, wornTool);
    }

    public List<Tool> createLightTools() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO, ToolState.from(newState));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, "Screwdriver", "U know for screws :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(5.99), usedState, "www.image.com/screwdriver");
        final Tool usedTool = Tool.from(usedToolDetailsDTO, ToolState.from(usedState));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, "Big hammer", "Just a bigger hammer", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(15.99), wornState, "www.image.com/big_hammer");
        final Tool wornTool = Tool.from(wornToolDetailsDTO, ToolState.from(wornState));

        return List.of(newTool, usedTool, wornTool);
    }

    public Tool createTool() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        return Tool.from(newToolDetailsDTO, ToolState.from(newState));
    }

    public ToolDetailsDTO createToolDetailsDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        return new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(), BigDecimal.valueOf(10.99), newState,
            "www.image.com/hammer");
    }

    public ToolDetailsDTO createEditedToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New");
        return new ToolDetailsDTO(1L, true, "Hammer#2", "It's a second hammer", ToolCategory.LIGHT.name(), BigDecimal.valueOf(5.99), newState,
            "www.image.com/second_hammer");
    }

    public ToolDTO createToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        return new ToolDTO("Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(), BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
    }

    public List<ToolDetailsDTO> createCommonNameToolDTOList() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, "Big hammer", "It's just a bigger hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/big_hammer");
        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, "Screwdriver", "Not a hammer", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/screwdriver");

        return List.of(newToolDetailsDTO, usedToolDetailsDTO, wornToolDetailsDTO);
    }

    public List<Tool> createCommonNameToolList() {
        return createCommonNameToolDTOList().stream()
            .map(toolDTO -> {
                final ToolState toolState = ToolState.from(toolDTO.toolState());
                return Tool.from(toolDTO, toolState);
            })
            .collect(Collectors.toList());
    }

    public Tool createChainsaw() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDTO chainsawDTO = new ToolDTO("Chainsaw", "Do you want to cut a big tree?", ToolCategory.LIGHT.name(), BigDecimal.valueOf(100.99),
            newState, "www.image.com/chainsaw");

        return Tool.from(chainsawDTO, ToolState.from(newState));
    }

    public Tool createLoader() {
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDTO usedToolDetailsDTO = new ToolDTO("Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");

        return Tool.from(usedToolDetailsDTO, ToolState.from(usedState));
    }

    public Tool createUnavailableTool() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, false, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        return Tool.from(newToolDetailsDTO, ToolState.from(newState));
    }

    public List<Tool> createHeavyToolsWithDate() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO, ToolState.from(newState), LocalDate.parse("2023-01-01"));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");
        final Tool usedTool = Tool.from(usedToolDetailsDTO, ToolState.from(usedState), LocalDate.parse("2023-01-02"));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/crane");
        final Tool wornTool = Tool.from(wornToolDetailsDTO, ToolState.from(wornState), LocalDate.parse("2023-01-03"));

        return List.of(newTool, usedTool, wornTool);
    }

    public List<Tool> createLightToolsWithDate() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO, ToolState.from(newState), LocalDate.parse("2023-01-01"));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, "Screwdriver", "U know for screws :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(5.99), usedState, "www.image.com/screwdriver");
        final Tool usedTool = Tool.from(usedToolDetailsDTO, ToolState.from(usedState), LocalDate.parse("2023-01-01"));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, "Big hammer", "Just a bigger hammer", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(15.99), wornState, "www.image.com/big_hammer");
        final Tool wornTool = Tool.from(wornToolDetailsDTO, ToolState.from(wornState), LocalDate.parse("2023-01-04"));

        return List.of(newTool, usedTool, wornTool);
    }
}
