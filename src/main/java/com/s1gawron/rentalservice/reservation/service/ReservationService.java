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
import com.s1gawron.rentalservice.shared.usercontext.UserContextProvider;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private static final String ELEMENT_NAME = "CUSTOMER RESERVATIONS";

    private final ReservationRepository reservationRepository;

    private final ReservationHasToolRepository reservationHasToolRepository;

    private final UserService userService;

    private final ToolService toolService;

    public ReservationService(final ReservationRepository reservationRepository, final ReservationHasToolRepository reservationHasToolRepository,
        final UserService userService, final ToolService toolService) {
        this.reservationRepository = reservationRepository;
        this.reservationHasToolRepository = reservationHasToolRepository;
        this.userService = userService;
        this.toolService = toolService;
    }

    @Transactional(readOnly = true)
    public ReservationListingDTO getUserReservations() {
        final User customer = getAndCheckIfUserIsCustomer();
        final List<ReservationDetailsDTO> userReservations = reservationRepository.findAllByCustomer(customer).stream()
            .map(reservation -> {
                final List<ToolDetailsDTO> toolDetails = toolService.getToolDetailsByReservationHasTools(reservation.getReservationHasTools());
                return reservation.toReservationDetailsDTO(toolDetails);
            })
            .toList();

        return new ReservationListingDTO(userReservations.size(), userReservations);
    }

    @Transactional(readOnly = true)
    public ReservationDetailsDTO getReservationDetails(final Long reservationId) {
        final User customer = getAndCheckIfUserIsCustomer();
        final Reservation reservationByIdAndCustomer = reservationRepository.findByReservationIdAndCustomer(reservationId, customer)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationId));
        final List<ToolDetailsDTO> toolDetails = toolService.getToolDetailsByReservationHasTools(reservationByIdAndCustomer.getReservationHasTools());

        return reservationByIdAndCustomer.toReservationDetailsDTO(toolDetails);
    }

    @Transactional
    public ReservationDetailsDTO makeReservation(final ReservationDTO reservationDTO) {
        ReservationDTOValidator.I.validate(reservationDTO);

        final User customer = getAndCheckIfUserIsCustomer();
        reservationDTO.toolIds().forEach(toolService::isToolAvailableOrRemoved);

        final Reservation reservation = Reservation.from(reservationDTO);
        reservation.addCustomer(customer);

        final AtomicReference<BigDecimal> reservationFinalPrice = new AtomicReference<>(BigDecimal.valueOf(0.00));
        final List<ToolDetailsDTO> toolDetails = new ArrayList<>();

        reservationDTO.toolIds().forEach(toolId -> {
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

        reservationRepository.findAllByCustomer(customer).add(savedReservation);
        userService.saveCustomerWithReservation(customer);

        return savedReservation.toReservationDetailsDTO(toolDetails);
    }

    @Transactional
    public ReservationDetailsDTO cancelReservation(final Long reservationId) {
        final User customer = getAndCheckIfUserIsCustomer();
        final Reservation reservationByIdAndCustomer = reservationRepository.findByReservationIdAndCustomer(reservationId, customer)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationId));
        final List<Tool> toolsFromReservation = toolService.getToolsByReservationHasTools(reservationByIdAndCustomer.getReservationHasTools());
        final List<ToolDetailsDTO> toolDetails = new ArrayList<>();

        toolsFromReservation.forEach(tool -> {
            toolService.makeToolAvailableAndSave(tool);
            toolDetails.add(tool.toToolDetailsDTO());
        });
        reservationByIdAndCustomer.cancelReservation();
        reservationRepository.save(reservationByIdAndCustomer);

        return reservationByIdAndCustomer.toReservationDetailsDTO(toolDetails);
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
        final User user = UserContextProvider.I.getLoggedInUser();

        if (user.getUserRole().equals(UserRole.WORKER)) {
            throw NoAccessForUserRoleException.create(ELEMENT_NAME);
        }

        return user;
    }
}
