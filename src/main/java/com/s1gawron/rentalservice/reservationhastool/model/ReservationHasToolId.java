package com.s1gawron.rentalservice.reservationhastool.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ReservationHasToolId implements Serializable {

    @Column(name = "tool_id")
    private Long toolId;

    @Column(name = "reservation_id")
    private Long reservationId;

}
