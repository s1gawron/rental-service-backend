package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.service.ToolService;
import org.springframework.data.domain.PageRequest;
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
    public ToolListingDTO getToolsByCategory(@PathVariable ToolCategory category,
        @RequestParam(defaultValue = "0") final int pageNumber,
        @RequestParam(defaultValue = "25") final int pageSize) {
        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return toolService.getToolsByCategory(category, pageRequest);
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
    public ToolListingDTO getToolsByName(@RequestBody final ToolSearchDTO toolSearchDTO, @RequestParam(defaultValue = "0") final int pageNumber,
        @RequestParam(defaultValue = "25") final int pageSize) {
        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return toolService.getToolsByName(toolSearchDTO, pageRequest);
    }

    @GetMapping("get/all")
    public ToolListingDTO getAllTools(@RequestParam(defaultValue = "0") final int pageNumber, @RequestParam(defaultValue = "25") final int pageSize) {
        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return toolService.getAllTools(pageRequest);
    }

}
