package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.service.ToolService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tool")
@AllArgsConstructor
public class ToolController extends ToolErrorHandlerController {

    private final ToolService toolService;

    @GetMapping("get/{category}")
    public ToolListingDTO getToolsByCategory(@PathVariable ToolCategory category) {
        return toolService.getToolsByCategory(category);
    }

    @GetMapping("get/new")
    public List<ToolDTO> getNewTools() {
        return toolService.getNewTools();
    }

    @GetMapping("get/{toolId}")
    public ToolDTO getToolById(@PathVariable final Long toolId) {
        return toolService.getToolById(toolId);
    }

    @PostMapping("add")
    public ToolDTO addTool(@RequestBody final ToolDTO toolDTO) {
        return toolService.validateAndAddTool(toolDTO);
    }

    @PutMapping("edit/{toolId}")
    public ToolDTO editTool(@PathVariable final Long toolId, @RequestBody final ToolDTO toolDTO) {
        return toolService.validateAndEditTool(toolId, toolDTO);
    }

    @DeleteMapping("delete/{toolId}")
    public boolean deleteTool(@PathVariable final Long toolId) {
        return toolService.deleteTool(toolId);
    }

}
