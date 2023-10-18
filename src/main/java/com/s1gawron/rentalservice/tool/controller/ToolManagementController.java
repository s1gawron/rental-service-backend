package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.service.ToolService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/management/tool/v1")
public class ToolManagementController extends ToolErrorHandlerController {

    private final ToolService toolService;

    public ToolManagementController(final ToolService toolService) {
        this.toolService = toolService;
    }

    @PostMapping("add")
    public ToolDetailsDTO addTool(@RequestBody final ToolDTO toolDTO) {
        return toolService.validateAndAddTool(toolDTO);
    }

    @PutMapping("edit")
    public ToolDetailsDTO editTool(@RequestBody final ToolDetailsDTO toolDetailsDTO) {
        return toolService.validateAndEditTool(toolDetailsDTO);
    }

    @DeleteMapping("delete/{toolId}")
    public boolean deleteTool(@PathVariable final Long toolId) {
        return toolService.deleteTool(toolId);
    }

}
