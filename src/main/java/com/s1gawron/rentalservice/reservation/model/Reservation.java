package com.s1gawron.rentalservice.reservation.model;

import com.s1gawron.rentalservice.reservation.dto.PlaceReservationDTO;
import com.s1gawron.rentalservice.reservationhastool.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private List<ReservationHasTool> tools;

    private Reservation(final LocalDate dateFrom, final LocalDate dateTo, final String additionalComment, final User customer) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.additionalComment = additionalComment;
        this.customer = customer;
    }

    public static Reservation from(final PlaceReservationDTO placeReservationDTO, final User customer) {
        return new Reservation(placeReservationDTO.getDateFrom(), placeReservationDTO.getDateTo(), placeReservationDTO.getAdditionalComment(), customer);
    }

    public void addTool(final Tool tool, final int toolQuantity) {
        final ReservationHasTool reservationHasTool = new ReservationHasTool(tool, this, toolQuantity);
        tools.add(reservationHasTool);
        tool.getReservations().add(reservationHasTool);
    }

    public void setReservationFinalPrice(final BigDecimal reservationFinalPrice) {
        this.reservationFinalPrice = reservationFinalPrice;
    }
}
