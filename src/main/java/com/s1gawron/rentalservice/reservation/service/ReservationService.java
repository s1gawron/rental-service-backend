package com.s1gawron.rentalservice.reservation.service;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.validator.ReservationDTOValidator;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.reservation.repository.ReservationHasToolRepository;
import com.s1gawron.rentalservice.reservation.repository.ReservationRepository;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationService {

    private static final String ELEMENT_NAME = "CUSTOMER RESERVATIONS";

    private final ReservationRepository reservationRepository;

    private final ReservationHasToolRepository reservationHasToolRepository;

    private final UserService userService;

    private final ToolService toolService;

    @Transactional(readOnly = true)
    public List<ReservationDetailsDTO> getUserReservations() {
        final User customer = getAndCheckIfUserIsCustomer();

        return reservationRepository.findAllByCustomer(customer).stream()
            .map(reservation -> {
                final List<ToolDetailsDTO> toolDetails = toolService.getToolDetailsByReservationHasTools(customer.getReservationHasTool());
                return reservation.toReservationDetailsDTO(toolDetails);
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDetailsDTO getReservationDetails(final Long reservationId) {
        final User customer = getAndCheckIfUserIsCustomer();
        final long doesReservationBelongToUser = customer.getCustomerReservations().stream()
            .filter(reservation -> reservation.getReservationId().equals(reservationId))
            .count();

        if (doesReservationBelongToUser == 0) {
            throw ReservationNotFoundException.create(reservationId);
        }

        final Reservation reservationById = reservationRepository.findByReservationId(reservationId)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationId));
        final List<ToolDetailsDTO> toolDetails = toolService.getToolDetailsByReservationHasTools(customer.getReservationHasTool());

        return reservationById.toReservationDetailsDTO(toolDetails);
    }

    @Transactional
    public ReservationDetailsDTO makeReservation(final ReservationDTO reservationDTO) {
        ReservationDTOValidator.I.validate(reservationDTO);

        final User customer = getAndCheckIfUserIsCustomer();
        reservationDTO.getToolIds().forEach(toolService::isToolAvailable);

        final Reservation reservation = Reservation.from(reservationDTO);
        reservation.addCustomer(customer);

        final AtomicReference<BigDecimal> reservationFinalPrice = new AtomicReference<>(BigDecimal.valueOf(0.00));
        final List<ToolDetailsDTO> toolDetails = new ArrayList<>();

        reservationDTO.getToolIds().forEach(toolId -> {
            final Tool tool = toolService.getToolById(toolId);
            final ReservationHasTool reservationHasTool = reservation.addTool(tool);
            final ReservationHasTool savedReservationHasTool = reservationHasToolRepository.save(reservationHasTool);

            tool.addReservation(savedReservationHasTool);
            toolService.saveToolWithReservation(tool);

            toolDetails.add(tool.toToolDetailsDTO());
            reservationFinalPrice.getAndUpdate(currentValue -> currentValue.add(tool.getPrice()));
        });

        reservation.setReservationFinalPrice(reservationFinalPrice.get());

        final Reservation savedReservation = reservationRepository.save(reservation);

        customer.addReservation(savedReservation);
        userService.saveCustomerWithReservation(customer);

        return savedReservation.toReservationDetailsDTO(toolDetails);
    }

    private User getAndCheckIfUserIsCustomer() {
        final String authenticatedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final User user = userService.getUserByEmail(authenticatedUserEmail).orElseThrow(() -> UserNotFoundException.create(authenticatedUserEmail));

        if (user.isWorker()) {
            throw NoAccessForUserRoleException.create(ELEMENT_NAME);
        }

        return user;
    }
}
