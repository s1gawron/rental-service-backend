package com.s1gawron.rentalservice.reservation.controller;

import com.s1gawron.rentalservice.reservation.dto.PlaceReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/customer")
@AllArgsConstructor
public class ReservationController extends ReservationErrorHandlerController {

    private final ReservationService reservationService;

    @GetMapping("reservations")
    public List<ReservationDTO> getUserReservations(@CurrentSecurityContext(expression = "authentication.email") final String email) {
        return reservationService.getUserReservations(email);
    }

    @PostMapping("reservation/place")
    public ReservationDTO placeReservation(@CurrentSecurityContext(expression = "authentication.email") final String email,
        @RequestBody final PlaceReservationDTO placeReservationDTO) {
        return reservationService.placeReservation(email, placeReservationDTO);
    }

}
