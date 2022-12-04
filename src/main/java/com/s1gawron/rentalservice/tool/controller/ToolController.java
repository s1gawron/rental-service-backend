package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.tool.dto.AddToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.service.ToolService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tool")
@AllArgsConstructor
public class ToolController extends ToolErrorHandlerController {

    private final ToolService toolService;

    @GetMapping("get/category/{category}")
    public ToolListingDTO getToolsByCategory(@PathVariable String category) {
        return toolService.getToolsByCategory(category);
    }

    @GetMapping("get/new")
    public List<ToolDTO> getNewTools() {
        return toolService.getNewTools();
    }

    @GetMapping("get/id/{toolId}")
    public ToolDTO getToolById(@PathVariable final Long toolId) {
        return toolService.getToolById(toolId);
    }

    @PostMapping("add")
    public ToolDTO addTool(@RequestBody final AddToolDTO addToolDTO) {
        return toolService.validateAndAddTool(addToolDTO);
    }

    @PutMapping("edit")
    public ToolDTO editTool(@RequestBody final ToolDTO toolDTO) {
        return toolService.validateAndEditTool(toolDTO);
    }

    @DeleteMapping("delete/{toolId}")
    public boolean deleteTool(@PathVariable final Long toolId) {
        return toolService.deleteTool(toolId);
    }

}
