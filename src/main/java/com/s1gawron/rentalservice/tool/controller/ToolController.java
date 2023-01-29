package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.service.ToolService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/public/tool")
public class ToolController extends ToolErrorHandlerController {

    private final ToolService toolService;

    public ToolController(final ToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping("get/category/{category}")
    public ToolListingDTO getToolsByCategory(@PathVariable String category) {
        return toolService.getToolsByCategory(category);
    }

    @GetMapping("get/new")
    public List<ToolDetailsDTO> getNewTools() {
        return toolService.getNewTools();
    }

    @GetMapping("get/id/{toolId}")
    public ToolDetailsDTO getToolById(@PathVariable final Long toolId) {
        return toolService.getToolDetails(toolId);
    }

    @PostMapping("get/name")
    public List<ToolDetailsDTO> getToolsByName(@RequestBody final ToolSearchDTO toolSearchDTO) {
        return toolService.getToolsByName(toolSearchDTO);
    }

}
