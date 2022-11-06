package com.s1gawron.rentalservice.reservation.service;

import com.s1gawron.rentalservice.reservation.dto.PlaceReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.repository.ReservationRepository;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.reservation.exception.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.user.exception.UserNotFoundException;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationService {

    private static final String CUSTOMER_RESERVATIONS = "customer reservations";

    private static final String RESERVATION_PLACEMENT = "reservation placement";

    private final ReservationRepository reservationRepository;

    private final UserService userService;

    private final ToolService toolService;

    @Transactional(readOnly = true)
    public List<ReservationDTO> getUserReservations(final String email) {
        final User user = userService.getUserByEmail(email).orElseThrow(() -> UserNotFoundException.create(email));

        if (user.isNotCustomer()) {
            throw NoAccessForUserRoleException.create(CUSTOMER_RESERVATIONS);
        }

        final List<ToolDTO> userTools = toolService.getToolsByReservations(user.getReservationHasTool());

        return user.getCustomerReservations().stream()
            .map(reservation -> ReservationDTO.from(reservation, userTools))
            .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDTO placeReservation(final String email, final PlaceReservationDTO placeReservationDTO) {
        final User user = userService.getUserByEmail(email).orElseThrow(() -> UserNotFoundException.create(email));

        if (user.isNotCustomer()) {
            throw NoAccessForUserRoleException.create(RESERVATION_PLACEMENT);
        }

        final Reservation reservation = Reservation.from(placeReservationDTO, user);
        final AtomicReference<BigDecimal> reservationFinalPrice = new AtomicReference<>();

        placeReservationDTO.getToolReservations().forEach(toolReservation -> {
            final Tool tool = Tool.from(toolReservation);
            reservation.addTool(tool, toolReservation.getQuantity());

            final BigDecimal toolReservationPrice = toolReservation.getPrice().multiply(BigDecimal.valueOf(toolReservation.getQuantity()));
            final BigDecimal addNextToolReservationPriceToFinalPrice = reservationFinalPrice.get().add(toolReservationPrice);
            reservationFinalPrice.set(addNextToolReservationPriceToFinalPrice);
            toolService.saveTool(tool);
        });

        reservation.setReservationFinalPrice(reservationFinalPrice.get());
        saveReservation(reservation);

        return ReservationDTO.from(reservation, placeReservationDTO.getToolReservations());
    }

    @Transactional(readOnly = true)
    void saveReservation(final Reservation reservation) {
        reservationRepository.save(reservation);
    }

}
