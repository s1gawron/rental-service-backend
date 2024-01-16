package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tool")
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_id", nullable = false, unique = true)
    private Long toolId;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    @Column(name = "is_removed", nullable = false)
    private boolean removed;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tool_category", nullable = false)
    private ToolCategory toolCategory;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Embedded
    private ToolState toolState;

    @Column(name = "date_added", nullable = false)
    private LocalDate dateAdded;

    @Column(name = "image_url")
    private String imageUrl;

    public Tool() {
    }

    private Tool(final boolean available, final boolean removed, final String name, final String description, final ToolCategory toolCategory,
        final BigDecimal price, final ToolState toolState, final LocalDate dateAdded, final String imageUrl) {
        this.available = available;
        this.removed = removed;
        this.name = name;
        this.description = description;
        this.toolCategory = toolCategory;
        this.price = price;
        this.toolState = toolState;
        this.dateAdded = dateAdded;
        this.imageUrl = imageUrl;
    }

    public static Tool from(final ToolDetailsDTO toolDetailsDTO) {
        return new Tool(toolDetailsDTO.available(), toolDetailsDTO.removed(), toolDetailsDTO.name(), toolDetailsDTO.description(),
            ToolCategory.findByValue(toolDetailsDTO.toolCategory()), toolDetailsDTO.price(), ToolState.from(toolDetailsDTO.toolState()), LocalDate.now(),
            toolDetailsDTO.imageUrl());
    }

    public static Tool from(final ToolDTO toolDTO) {
        return new Tool(true, false, toolDTO.name(), toolDTO.description(), ToolCategory.findByValue(toolDTO.toolCategory()), toolDTO.price(),
            ToolState.from(toolDTO.toolState()), LocalDate.now(), toolDTO.imageUrl());
    }

    public static Tool from(final ToolDetailsDTO toolDetailsDTO, final LocalDate dateAdded) {
        return new Tool(true, false, toolDetailsDTO.name(), toolDetailsDTO.description(), ToolCategory.findByValue(toolDetailsDTO.toolCategory()),
            toolDetailsDTO.price(), ToolState.from(toolDetailsDTO.toolState()), dateAdded, toolDetailsDTO.imageUrl());
    }

    public ToolDetailsDTO toToolDetailsDTO() {
        return new ToolDetailsDTO(this.toolId, this.available, this.removed, this.name, this.description, this.toolCategory.name(), this.price,
            new ToolStateDTO(this.toolState.getStateType().name(), this.toolState.getStateDescription()), this.imageUrl);
    }

    public void edit(final ToolDetailsDTO toolDetailsDTO) {
        this.name = toolDetailsDTO.name();
        this.available = toolDetailsDTO.available();
        this.removed = toolDetailsDTO.removed();
        this.description = toolDetailsDTO.description();
        this.toolCategory = ToolCategory.findByValue(toolDetailsDTO.toolCategory());
        this.price = toolDetailsDTO.price();
        this.toolState = ToolState.from(toolDetailsDTO.toolState());
        this.imageUrl = toolDetailsDTO.imageUrl();
    }

    public void remove() {
        this.available = false;
        this.removed = true;
    }

    public void makeToolUnavailable() {
        this.available = false;
    }

    public void makeToolAvailable() {
        this.available = true;
    }

    public Long getToolId() {
        return toolId;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ToolCategory getToolCategory() {
        return toolCategory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ToolState getToolState() {
        return toolState;
    }

    public boolean isRemoved() {
        return removed;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
