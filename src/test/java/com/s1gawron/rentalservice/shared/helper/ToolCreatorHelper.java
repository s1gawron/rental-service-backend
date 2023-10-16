package com.s1gawron.rentalservice.shared.helper;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolStateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public enum ToolCreatorHelper {

    I;

    private static final String REMOVED_SUFFIX = "removed";

    public List<ToolDetailsDTO> createToolDTOList() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, false, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, false, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");
        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, false, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/crane");

        return List.of(newToolDetailsDTO, usedToolDetailsDTO, wornToolDetailsDTO);
    }

    public List<Tool> createToolList() {
        return createToolDTOList().stream()
            .map(Tool::from)
            .toList();
    }

    public List<Tool> createHeavyTools() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, false, "Heavy Hammer", "It's just a heavy hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO);

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, false, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");
        final Tool usedTool = Tool.from(usedToolDetailsDTO);

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, false, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/crane");
        final Tool wornTool = Tool.from(wornToolDetailsDTO);

        return List.of(newTool, usedTool, wornTool);
    }

    public List<Tool> createLightTools() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, false, "Light Hammer", "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO);

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, false, "Screwdriver", "U know for screws :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(5.99), usedState, "www.image.com/screwdriver");
        final Tool usedTool = Tool.from(usedToolDetailsDTO);

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, false, "Big hammer", "Just a bigger hammer", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(15.99), wornState, "www.image.com/big_hammer");
        final Tool wornTool = Tool.from(wornToolDetailsDTO);

        return List.of(newTool, usedTool, wornTool);
    }

    public Tool createTool() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, false, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        return Tool.from(newToolDetailsDTO);
    }

    public ToolDetailsDTO createToolDetailsDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        return new ToolDetailsDTO(1L, true, false, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(), BigDecimal.valueOf(10.99), newState,
            "www.image.com/hammer");
    }

    public ToolDetailsDTO createEditedToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New");
        return new ToolDetailsDTO(1L, true, false, "Hammer#2", "It's a second hammer", ToolCategory.LIGHT.name(), BigDecimal.valueOf(5.99), newState,
            "www.image.com/second_hammer");
    }

    public ToolDTO createToolDTO() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        return new ToolDTO("Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(), BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
    }

    public List<ToolDetailsDTO> createCommonNameToolDTOList(final boolean removed) {
        String newToolName = "Hammer";
        String usedToolName = "Big hammer";
        String wornToolName = "Screwdriver";

        if (removed) {
            newToolName = newToolName + REMOVED_SUFFIX;
            usedToolName = usedToolName + REMOVED_SUFFIX;
            wornToolName = wornToolName + REMOVED_SUFFIX;
        }

        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, removed, newToolName, "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, removed, usedToolName, "It's just a bigger hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/big_hammer");

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, removed, wornToolName, "Not a hammer", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/screwdriver");

        return List.of(newToolDetailsDTO, usedToolDetailsDTO, wornToolDetailsDTO);
    }

    public List<Tool> createCommonNameToolList(final boolean removed) {
        return createCommonNameToolDTOList(removed).stream()
            .map(Tool::from)
            .toList();
    }

    public Tool createChainsaw() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDTO chainsawDTO = new ToolDTO("Chainsaw", "Do you want to cut a big tree?", ToolCategory.LIGHT.name(), BigDecimal.valueOf(100.99),
            newState, "www.image.com/chainsaw");

        return Tool.from(chainsawDTO);
    }

    public Tool createLoader() {
        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDTO usedToolDetailsDTO = new ToolDTO("Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");

        return Tool.from(usedToolDetailsDTO);
    }

    public Tool createUnavailableTool() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, false, false, "Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        return Tool.from(newToolDetailsDTO);
    }

    public List<Tool> createHeavyToolsWithDate() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, false, "Heavy Hammer", "It's just a hammer :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO, LocalDate.parse("2023-01-01"));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, false, "Loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");
        final Tool usedTool = Tool.from(usedToolDetailsDTO, LocalDate.parse("2023-01-02"));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, false, "Crane", "Mechanical giraffe", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/crane");
        final Tool wornTool = Tool.from(wornToolDetailsDTO, LocalDate.parse("2023-01-03"));

        return List.of(newTool, usedTool, wornTool);
    }

    public List<Tool> createLightToolsWithDate() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newToolDetailsDTO = new ToolDetailsDTO(1L, true, false, "Hammer", "It's just a hammer :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newTool = Tool.from(newToolDetailsDTO, LocalDate.parse("2023-01-01"));

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedToolDetailsDTO = new ToolDetailsDTO(2L, true, false, "Screwdriver", "U know for screws :)", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(5.99), usedState, "www.image.com/screwdriver");
        final Tool usedTool = Tool.from(usedToolDetailsDTO, LocalDate.parse("2023-01-01"));

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornToolDetailsDTO = new ToolDetailsDTO(3L, true, false, "Big hammer", "Just a bigger hammer", ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(15.99), wornState, "www.image.com/big_hammer");
        final Tool wornTool = Tool.from(wornToolDetailsDTO, LocalDate.parse("2023-01-04"));

        return List.of(newTool, usedTool, wornTool);
    }

    public List<Tool> createRemovedTools() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newHeavyToolDetailsDTO = new ToolDetailsDTO(1L, true, true, "Removed heavy hammer", "It's just a hammer :)",
            ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newHeavyTool = Tool.from(newHeavyToolDetailsDTO);

        final ToolStateDTO usedState = new ToolStateDTO(ToolStateType.MINIMAL_WEAR.name(), "No signs of usage");
        final ToolDetailsDTO usedHeavyToolDetailsDTO = new ToolDetailsDTO(2L, true, true, "Removed loader", "4 wheeled loader :)", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(1000.99), usedState, "www.image.com/loader");
        final Tool usedHeavyTool = Tool.from(usedHeavyToolDetailsDTO);

        final ToolStateDTO wornState = new ToolStateDTO(ToolStateType.WELL_WORN.name(), "Rusty");
        final ToolDetailsDTO wornHeavyToolDetailsDTO = new ToolDetailsDTO(3L, true, true, "Removed crane", "Mechanical giraffe", ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(19999.99), wornState, "www.image.com/crane");
        final Tool wornHeavyTool = Tool.from(wornHeavyToolDetailsDTO);

        final ToolDetailsDTO newLightToolDetailsDTO = new ToolDetailsDTO(1L, true, true, "Removed light hammer", "It's just a hammer :)",
            ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");
        final Tool newLightTool = Tool.from(newLightToolDetailsDTO);

        final ToolDetailsDTO usedLightToolDetailsDTO = new ToolDetailsDTO(2L, true, true, "Removed screwdriver", "U know for screws :)",
            ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(5.99), usedState, "www.image.com/screwdriver");
        final Tool usedLightTool = Tool.from(usedLightToolDetailsDTO);

        final ToolDetailsDTO wornLightToolDetailsDTO = new ToolDetailsDTO(3L, true, true, "Removed big hammer", "Just a bigger hammer",
            ToolCategory.LIGHT.name(),
            BigDecimal.valueOf(15.99), wornState, "www.image.com/big_hammer");
        final Tool wornLightTool = Tool.from(wornLightToolDetailsDTO);

        return List.of(newHeavyTool, newLightTool, usedHeavyTool, usedLightTool, wornHeavyTool, wornLightTool);
    }

    public Tool createRemovedHammerWithAvailability() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.name(), "New and shiny tool");
        final ToolDetailsDTO newHeavyToolDetailsDTO = new ToolDetailsDTO(1L, true, true, "Removed heavy hammer", "It's just a hammer :)",
            ToolCategory.HEAVY.name(),
            BigDecimal.valueOf(10.99), newState, "www.image.com/hammer");

        return Tool.from(newHeavyToolDetailsDTO);
    }
}
