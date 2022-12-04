package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.reservationhastool.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.dto.AddToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private List<ReservationHasTool> reservationsHasTools;

    private Tool(final String name, final String description, final ToolCategory toolCategory, final BigDecimal price, final ToolState toolState,
        final LocalDate dateAdded) {
        this.name = name;
        this.description = description;
        this.toolCategory = toolCategory;
        this.price = price;
        this.toolState = toolState;
        this.dateAdded = dateAdded;
    }

    public static Tool from(final ToolDTO toolDTO, final ToolState toolState) {
        return new Tool(toolDTO.getName(), toolDTO.getDescription(), toolDTO.getToolCategory(), toolDTO.getPrice(), toolState, LocalDate.now());
    }

    public static Tool from(final AddToolDTO addToolDTO, final ToolState toolState) {
        return new Tool(addToolDTO.getName(), addToolDTO.getDescription(), addToolDTO.getToolCategory(), addToolDTO.getPrice(), toolState, LocalDate.now());
    }

    public void edit(final ToolDTO toolDTO) {
        this.name = toolDTO.getName();
        this.description = toolDTO.getDescription();
        this.toolCategory = toolDTO.getToolCategory();
        this.price = toolDTO.getPrice();
    }
}
