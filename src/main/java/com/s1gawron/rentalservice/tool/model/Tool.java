package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tool")
@DynamicUpdate
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_id")
    private Long toolId;

    @Column(name = "is_available")
    private boolean available;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tool_category")
    private ToolCategory toolCategory;

    @Column(name = "price")
    private BigDecimal price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tool_state_id", referencedColumnName = "tool_state_id")
    private ToolState toolState;

    @Column(name = "date_added")
    private LocalDate dateAdded;

    @OneToMany(mappedBy = "tool")
    private List<ReservationHasTool> reservationHasTools;

    @Column(name = "image_url")
    private String imageUrl;

    public Tool() {
    }

    private Tool(final boolean available, final String name, final String description, final ToolCategory toolCategory, final BigDecimal price,
        final ToolState toolState, final LocalDate dateAdded, final String imageUrl) {
        this.available = available;
        this.name = name;
        this.description = description;
        this.toolCategory = toolCategory;
        this.price = price;
        this.toolState = toolState;
        this.dateAdded = dateAdded;
        this.imageUrl = imageUrl;
    }

    public static Tool from(final ToolDetailsDTO toolDetailsDTO, final ToolState toolState) {
        return new Tool(toolDetailsDTO.available(), toolDetailsDTO.name(), toolDetailsDTO.description(),
            ToolCategory.findByValue(toolDetailsDTO.toolCategory()), toolDetailsDTO.price(), toolState, LocalDate.now(), toolDetailsDTO.imageUrl());
    }

    public static Tool from(final ToolDTO toolDTO, final ToolState toolState) {
        return new Tool(true, toolDTO.name(), toolDTO.description(), ToolCategory.findByValue(toolDTO.toolCategory()), toolDTO.price(),
            toolState, LocalDate.now(), toolDTO.imageUrl());
    }

    public static Tool from(final ToolDetailsDTO toolDetailsDTO, final ToolState toolState, final LocalDate dateAdded) {
        return new Tool(true, toolDetailsDTO.name(), toolDetailsDTO.description(), ToolCategory.findByValue(toolDetailsDTO.toolCategory()),
            toolDetailsDTO.price(), toolState, dateAdded, toolDetailsDTO.imageUrl());
    }

    public ToolDetailsDTO toToolDetailsDTO() {
        return new ToolDetailsDTO(this.toolId, this.available, this.name, this.description, this.toolCategory.name(), this.price,
            new ToolStateDTO(this.toolState.getStateType().name(), this.toolState.getDescription()), this.imageUrl);
    }

    public void edit(final ToolDetailsDTO toolDetailsDTO) {
        this.name = toolDetailsDTO.name();
        this.available = toolDetailsDTO.available();
        this.description = toolDetailsDTO.description();
        this.toolCategory = ToolCategory.findByValue(toolDetailsDTO.toolCategory());
        this.price = toolDetailsDTO.price();
        this.imageUrl = toolDetailsDTO.imageUrl();
    }

    public void addReservation(final ReservationHasTool reservationHasTool) {
        if (this.reservationHasTools == null) {
            this.reservationHasTools = new ArrayList<>();
        }
        this.reservationHasTools.add(reservationHasTool);
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

    public String getImageUrl() {
        return imageUrl;
    }
}
