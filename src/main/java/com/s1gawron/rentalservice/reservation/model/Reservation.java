package com.s1gawron.rentalservice.reservation.model;

import com.s1gawron.rentalservice.reservationhastool.model.ReservationHasTool;
import com.s1gawron.rentalservice.user.model.User;
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
    @JoinColumn(name="user_id", nullable=false)
    private User customer;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationHasTool> tools;

}
