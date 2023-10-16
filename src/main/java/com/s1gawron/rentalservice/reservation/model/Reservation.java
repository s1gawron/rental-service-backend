package com.s1gawron.rentalservice.reservation.model;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservationtool.model.ReservationTool;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.user.model.User;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    @Column(name = "is_expired", nullable = false)
    private boolean expired;

    @Column(name = "is_canceled", nullable = false)
    private boolean canceled;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    @Column(name = "reservation_final_price", nullable = false)
    private BigDecimal reservationFinalPrice;

    @Column(name = "reservation_additional_comment")
    private String additionalComment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationTool> reservationTools;

    public Reservation() {
    }

    private Reservation(final LocalDate dateFrom, final LocalDate dateTo, final String additionalComment) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.additionalComment = additionalComment;
    }

    private Reservation(final Long reservationId, final boolean expired, final boolean canceled, final LocalDate dateFrom, final LocalDate dateTo,
        final BigDecimal reservationFinalPrice,
        final String additionalComment) {
        this.reservationId = reservationId;
        this.expired = expired;
        this.canceled = canceled;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.reservationFinalPrice = reservationFinalPrice;
        this.additionalComment = additionalComment;
    }

    public static Reservation from(final ReservationDTO reservationDTO) {
        return new Reservation(reservationDTO.dateFrom(), reservationDTO.dateTo(), reservationDTO.additionalComment());
    }

    public static Reservation from(final ReservationDetailsDTO reservationDetailsDTO) {
        return new Reservation(reservationDetailsDTO.reservationId(), reservationDetailsDTO.expired(), reservationDetailsDTO.canceled(),
            reservationDetailsDTO.dateFrom(), reservationDetailsDTO.dateTo(), reservationDetailsDTO.reservationFinalPrice(),
            reservationDetailsDTO.additionalComment());
    }

    public ReservationTool addTool(final Tool tool) {
        final ReservationTool reservationTool = new ReservationTool(this, tool);

        if (this.reservationTools == null) {
            this.reservationTools = new ArrayList<>();
        }

        this.reservationTools.add(reservationTool);

        return reservationTool;
    }

    public void addCustomer(final User customer) {
        this.customer = customer;
    }

    public void setReservationFinalPrice(final BigDecimal reservationFinalPrice) {
        this.reservationFinalPrice = reservationFinalPrice;
    }

    public ReservationDetailsDTO toReservationDetailsDTO(final List<ToolDetailsDTO> toolDetails) {
        return new ReservationDetailsDTO(this.reservationId, this.expired, this.canceled, this.dateFrom, this.dateTo, this.reservationFinalPrice,
            this.additionalComment, toolDetails);
    }

    public void cancelReservation() {
        this.canceled = true;
    }

    public void expireReservation() {
        this.expired = true;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public List<ReservationTool> getReservationTools() {
        return reservationTools;
    }
}
