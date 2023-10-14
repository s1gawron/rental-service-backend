package com.s1gawron.rentalservice.reservation.model;

import com.s1gawron.rentalservice.tool.model.Tool;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation_has_tool")
public class ReservationHasTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_has_tool", nullable = false, unique = true)
    private Long reservationHasToolId;

    @ManyToOne
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    public ReservationHasTool() {
    }

    public ReservationHasTool(final Tool tool, final Reservation reservation) {
        this.tool = tool;
        this.reservation = reservation;
    }
}
