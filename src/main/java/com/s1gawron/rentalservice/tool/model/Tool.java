package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.reservationhastool.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
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
    @Column(name = "tool_type")
    private ToolType toolType;

    @Column(name = "price")
    private BigDecimal price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tool_state_id", referencedColumnName = "tool_state_id")
    private ToolState toolState;

    @OneToMany(mappedBy = "tool")
    private List<ReservationHasTool> reservations;

    public Tool(final String name, final String description, final ToolType toolType, final BigDecimal price, final ToolState toolState) {
        this.name = name;
        this.description = description;
        this.toolType = toolType;
        this.price = price;
        this.toolState = toolState;
    }

    public static Tool from(final ToolDTO toolReservation) {
        return new Tool(toolReservation.getName(), toolReservation.getDescription(), toolReservation.getToolType(), toolReservation.getPrice(),
            toolReservation.getToolState());
    }
}
