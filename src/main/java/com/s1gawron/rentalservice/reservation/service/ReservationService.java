package com.s1gawron.rentalservice.reservation.service;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {

    private static final String ELEMENT_NAME = "CUSTOMER RESERVATIONS";

    private final ReservationRepository reservationRepository;

    private final ReservationHasToolRepository reservationHasToolRepository;

    private final UserService userService;

    private final ToolService toolService;

    @Transactional(readOnly = true)
    public ReservationListingDTO getUserReservations() {
        final User customer = getAndCheckIfUserIsCustomer();
        final List<ReservationDetailsDTO> userReservations = reservationRepository.findAllByCustomer(customer).stream()
            .map(reservation -> {
                final List<ToolDetailsDTO> toolDetails = toolService.getToolDetailsByReservationHasTools(reservation.getReservationHasTools());
                return reservation.toReservationDetailsDTO(toolDetails);
            })
            .collect(Collectors.toList());

        return ReservationListingDTO.create(userReservations);
    }

    @Transactional(readOnly = true)
    public ReservationDetailsDTO getReservationDetails(final Long reservationId) {
        final User customer = getAndCheckIfUserIsCustomer();
        customer.doesReservationBelongToUser(reservationId);

        final Reservation reservationById = reservationRepository.findByReservationId(reservationId)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationId));
        final List<ToolDetailsDTO> toolDetails = toolService.getToolDetailsByReservationHasTools(reservationById.getReservationHasTools());

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
            toolService.makeToolUnavailableAndSave(tool);

            toolDetails.add(tool.toToolDetailsDTO());
            reservationFinalPrice.getAndUpdate(currentValue -> currentValue.add(tool.getPrice()));
        });

        reservation.setReservationFinalPrice(reservationFinalPrice.get());

        final Reservation savedReservation = reservationRepository.save(reservation);

        customer.addReservation(savedReservation);
        userService.saveCustomerWithReservation(customer);

        return savedReservation.toReservationDetailsDTO(toolDetails);
    }

    @Transactional
    public ReservationDetailsDTO cancelReservation(final Long reservationId) {
        final User customer = getAndCheckIfUserIsCustomer();
        customer.doesReservationBelongToUser(reservationId);

        final Reservation reservationById = reservationRepository.findByReservationId(reservationId)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationId));
        final List<Tool> toolsFromReservation = toolService.getToolsByReservationHasTools(reservationById.getReservationHasTools());
        final List<ToolDetailsDTO> toolDetails = new ArrayList<>();

        toolsFromReservation.forEach(tool -> {
            toolService.makeToolAvailableAndSave(tool);
            toolDetails.add(tool.toToolDetailsDTO());
        });
        reservationById.cancelReservation();
        reservationRepository.save(reservationById);

        return reservationById.toReservationDetailsDTO(toolDetails);
    }

    @Transactional(readOnly = true)
    public List<Long> getReservationIds() {
        return reservationRepository.getAllIds();
    }

    @Transactional
    public void checkReservationsExpiryStatus(final List<Long> reservationIds) {
        final List<Reservation> reservationsById = reservationRepository.findAllById(reservationIds);

        reservationsById.forEach(reservation -> {
            final LocalDate today = LocalDate.now();

            if (today.isAfter(reservation.getDateTo())) {
                log.info("Reservation#{} expired, performing clean job", reservation.getReservationId());

                final List<Tool> toolsFromReservation = toolService.getToolsByReservationHasTools(reservation.getReservationHasTools());
                toolsFromReservation.forEach(toolService::makeToolAvailableAndSave);
                reservation.expireReservation();
                reservationRepository.save(reservation);

                log.info("Clean job for reservation#{} finished successfully", reservation.getReservationId());
            }
        });
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
