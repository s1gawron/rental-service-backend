package com.s1gawron.rentalservice.reservation.controller;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/customer/reservation")
@AllArgsConstructor
public class ReservationController extends ReservationErrorHandlerController {

    private final ReservationService reservationService;

    @GetMapping("get/all")
    public ReservationListingDTO getUserReservations() {
        return reservationService.getUserReservations();
    }

    @GetMapping("get/id/{reservationId}")
    public ReservationDetailsDTO getReservationById(@PathVariable final Long reservationId) {
        return reservationService.getReservationDetails(reservationId);
    }

    @PostMapping("make")
    public ReservationDetailsDTO makeReservation(@RequestBody final ReservationDTO reservationDTO) {
        return reservationService.makeReservation(reservationDTO);
    }

    @PostMapping("cancel/{reservationId}")
    public ReservationDetailsDTO cancelReservation(@PathVariable final Long reservationId) {
        return reservationService.cancelReservation(reservationId);
    }

}
