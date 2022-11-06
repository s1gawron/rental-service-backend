package com.s1gawron.rentalservice.reservationhastool.model;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.tool.model.Tool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "reservation_has_tool")
@DynamicUpdate
@NoArgsConstructor
@Getter
public class ReservationHasTool {

    @EmbeddedId
    private ReservationHasToolId reservationHasToolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("toolId")
    private Tool tool;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reservationId")
    private Reservation reservation;

    @Column(name = "tool_quantity")
    private int toolQuantity;

    public ReservationHasTool(final Tool tool, final Reservation reservation, final int toolQuantity) {
        this.tool = tool;
        this.reservation = reservation;
        this.toolQuantity = toolQuantity;
    }
}
