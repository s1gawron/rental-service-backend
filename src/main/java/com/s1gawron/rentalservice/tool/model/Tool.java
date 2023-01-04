package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tool")
@DynamicUpdate
@NoArgsConstructor
@Getter
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
        return new Tool(toolDetailsDTO.getAvailable(), toolDetailsDTO.getName(), toolDetailsDTO.getDescription(),
            ToolCategory.findByValue(toolDetailsDTO.getToolCategory()), toolDetailsDTO.getPrice(), toolState, LocalDate.now(), toolDetailsDTO.getImageUrl());
    }

    public static Tool from(final ToolDTO toolDTO, final ToolState toolState) {
        return new Tool(true, toolDTO.getName(), toolDTO.getDescription(), ToolCategory.findByValue(toolDTO.getToolCategory()), toolDTO.getPrice(),
            toolState, LocalDate.now(), toolDTO.getImageUrl());
    }

    public static Tool from(final ToolDetailsDTO toolDetailsDTO, final ToolState toolState, final LocalDate dateAdded) {
        return new Tool(true, toolDetailsDTO.getName(), toolDetailsDTO.getDescription(), ToolCategory.findByValue(toolDetailsDTO.getToolCategory()),
            toolDetailsDTO.getPrice(), toolState, dateAdded, toolDetailsDTO.getImageUrl());
    }

    public ToolDetailsDTO toToolDetailsDTO() {
        return new ToolDetailsDTO(this.toolId, this.available, this.name, this.description, this.toolCategory.getName(), this.price,
            ToolStateDTO.from(this.toolState), this.imageUrl);
    }

    public void edit(final ToolDetailsDTO toolDetailsDTO) {
        this.name = toolDetailsDTO.getName();
        this.available = toolDetailsDTO.getAvailable();
        this.description = toolDetailsDTO.getDescription();
        this.toolCategory = ToolCategory.findByValue(toolDetailsDTO.getToolCategory());
        this.price = toolDetailsDTO.getPrice();
        this.imageUrl = toolDetailsDTO.getImageUrl();
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
}
