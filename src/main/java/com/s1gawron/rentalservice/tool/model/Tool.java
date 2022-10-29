package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.reservationhastool.model.ReservationHasTool;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tool")
@DynamicUpdate
@NoArgsConstructor
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

}
