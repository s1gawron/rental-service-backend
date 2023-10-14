package com.s1gawron.rentalservice.reservation.controller;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/customer/reservation/v1")
public class ReservationController extends ReservationErrorHandlerController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("get/all")
    public ReservationListingDTO getUserReservations(@RequestParam(defaultValue = "0") final int pageNumber,
        @RequestParam(defaultValue = "25") final int pageSize) {
        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return reservationService.getUserReservations(pageRequest);
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
