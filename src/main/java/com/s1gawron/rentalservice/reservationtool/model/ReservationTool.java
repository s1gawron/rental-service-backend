package com.s1gawron.rentalservice.reservationtool.model;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_tool")
public class ReservationTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resetvation_tool_id", nullable = false, unique = true)
    private long reservationToolId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @OneToOne
    @JoinColumn(name = "reservation_tool_id", referencedColumnName = "tool_id", nullable = false)
    private Tool tool;

    @Column(name = "price_on_reservation", nullable = false)
    private BigDecimal priceOnReservation;

    public ReservationTool() {
    }

    public ReservationTool(final Reservation reservation, final Tool tool) {
        this.reservation = reservation;
        this.tool = tool;
        this.priceOnReservation = tool.getPrice();
    }

    public Tool getTool() {
        return tool;
    }

    public ToolDetailsDTO toToolDetailsDTO() {
        final Tool reservationTool = this.tool;
        final ToolStateDTO reservationToolState = new ToolStateDTO(reservationTool.getToolState().getStateType().name(),
            reservationTool.getToolState().getStateDescription());

        return new ToolDetailsDTO(reservationTool.getToolId(), reservationTool.isAvailable(), reservationTool.isRemoved(), reservationTool.getName(),
            reservationTool.getDescription(), reservationTool.getToolCategory().name(), this.priceOnReservation, reservationToolState,
            reservationTool.getImageUrl());
    }
}
