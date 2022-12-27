package com.s1gawron.rentalservice.reservation.model;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@DynamicUpdate
@NoArgsConstructor
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "reservation_final_price")
    private BigDecimal reservationFinalPrice;

    @Column(name = "reservation_additional_comment")
    private String additionalComment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationHasTool> reservationHasTools;

    private Reservation(final LocalDate dateFrom, final LocalDate dateTo, final String additionalComment) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.additionalComment = additionalComment;
    }

    private Reservation(final Long reservationId, final LocalDate dateFrom, final LocalDate dateTo, final BigDecimal reservationFinalPrice,
        final String additionalComment) {
        this.reservationId = reservationId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.reservationFinalPrice = reservationFinalPrice;
        this.additionalComment = additionalComment;
    }

    public static Reservation from(final ReservationDTO reservationDTO) {
        return new Reservation(reservationDTO.getDateFrom(), reservationDTO.getDateTo(), reservationDTO.getAdditionalComment());
    }

    public static Reservation from(final ReservationDetailsDTO reservationDetailsDTO) {
        return new Reservation(reservationDetailsDTO.getReservationId(), reservationDetailsDTO.getDateFrom(), reservationDetailsDTO.getDateTo(),
            reservationDetailsDTO.getReservationFinalPrice(), reservationDetailsDTO.getAdditionalComment());
    }

    public ReservationHasTool addTool(final Tool tool) {
        final ReservationHasTool reservationHasTool = new ReservationHasTool(tool, this);

        if (this.reservationHasTools == null) {
            this.reservationHasTools = new ArrayList<>();
        }

        this.reservationHasTools.add(reservationHasTool);

        return reservationHasTool;
    }

    public void addCustomer(final User customer) {
        this.customer = customer;
    }

    public void setReservationFinalPrice(final BigDecimal reservationFinalPrice) {
        this.reservationFinalPrice = reservationFinalPrice;
    }

    public ReservationDetailsDTO toReservationDetailsDTO(final List<ToolDetailsDTO> toolDetails) {
        return new ReservationDetailsDTO(this.reservationId, this.dateFrom, this.dateTo, this.reservationFinalPrice, this.additionalComment, toolDetails);
    }
}
