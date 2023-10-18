package com.s1gawron.rentalservice.reservation.service;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
import com.s1gawron.rentalservice.reservation.dto.validator.ReservationDTOValidator;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.model.ReservationStatus;
import com.s1gawron.rentalservice.reservation.repository.ReservationDAO;
import com.s1gawron.rentalservice.reservationtool.model.ReservationTool;
import com.s1gawron.rentalservice.reservationtool.service.ReservationToolService;
import com.s1gawron.rentalservice.shared.usercontext.UserContextProvider;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ReservationService {

    private final ReservationDAO reservationDAO;

    private final ReservationToolService reservationToolService;

    private final ToolService toolService;

    public ReservationService(final ReservationDAO reservationDAO, final ReservationToolService reservationToolService, final ToolService toolService) {
        this.reservationDAO = reservationDAO;
        this.reservationToolService = reservationToolService;
        this.toolService = toolService;
    }

    @Transactional(readOnly = true)
    public ReservationListingDTO getUserReservations(final Pageable pageable) {
        final User customer = UserContextProvider.I.getLoggedInUser();
        final Page<Reservation> allReservations = reservationDAO.findAllByCustomer(customer, pageable);
        final List<ReservationDetailsDTO> userReservations = allReservations.stream()
            .map(reservation -> {
                final List<ToolDetailsDTO> toolDetails = reservation.getReservationTools().stream()
                    .map(ReservationTool::toToolDetailsDTO)
                    .toList();
                return reservation.toReservationDetailsDTO(toolDetails);
            })
            .toList();

        return new ReservationListingDTO(allReservations.getTotalPages(), (int) allReservations.getTotalElements(), userReservations);
    }

    @Transactional(readOnly = true)
    public ReservationDetailsDTO getReservationDetails(final Long reservationId) {
        final User customer = UserContextProvider.I.getLoggedInUser();
        final Reservation reservationByIdAndCustomer = reservationDAO.findByReservationIdAndCustomer(reservationId, customer)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationId));
        final List<ToolDetailsDTO> toolDetails = reservationByIdAndCustomer.getReservationTools().stream()
            .map(ReservationTool::toToolDetailsDTO)
            .toList();

        return reservationByIdAndCustomer.toReservationDetailsDTO(toolDetails);
    }

    @Transactional
    public ReservationDetailsDTO makeReservation(final ReservationDTO reservationDTO) {
        ReservationDTOValidator.I.validate(reservationDTO);

        final User customer = UserContextProvider.I.getLoggedInUser();
        reservationDTO.toolIds().forEach(toolService::isToolAvailableOrRemoved);

        final Reservation reservation = Reservation.from(reservationDTO);
        reservation.addCustomer(customer);

        final AtomicReference<BigDecimal> reservationFinalPrice = new AtomicReference<>(BigDecimal.valueOf(0.00));
        final List<ToolDetailsDTO> toolDetails = new ArrayList<>();
        final ArrayList<ReservationTool> reservationTools = new ArrayList<>();

        reservationDTO.toolIds().forEach(toolId -> {
            final Tool tool = toolService.getToolById(toolId);
            final ReservationTool reservationTool = reservation.addTool(tool);

            reservationTools.add(reservationTool);
            toolService.makeToolUnavailableAndSave(tool);

            toolDetails.add(reservationTool.toToolDetailsDTO());
            reservationFinalPrice.getAndUpdate(currentValue -> currentValue.add(tool.getPrice()));
        });

        reservation.setReservationFinalPrice(reservationFinalPrice.get());

        final Reservation savedReservation = reservationDAO.save(reservation);
        reservationToolService.saveAll(reservationTools);

        return savedReservation.toReservationDetailsDTO(toolDetails);
    }

    @Transactional
    public ReservationDetailsDTO cancelReservation(final Long reservationId) {
        final User customer = UserContextProvider.I.getLoggedInUser();
        final Reservation reservationByIdAndCustomer = reservationDAO.findByReservationIdAndCustomer(reservationId, customer)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationId));
        final List<ToolDetailsDTO> toolDetails = new ArrayList<>();

        reservationByIdAndCustomer.getReservationTools().forEach(reservationTool -> {
            toolService.makeToolAvailableAndSave(reservationTool.getTool());
            toolDetails.add(reservationTool.toToolDetailsDTO());
        });

        reservationByIdAndCustomer.cancelReservation();
        reservationDAO.save(reservationByIdAndCustomer);

        return reservationByIdAndCustomer.toReservationDetailsDTO(toolDetails);
    }

    @Transactional
    public void completeReservation(final Long reservationIdToComplete) {
        final Reservation reservation = reservationDAO.findByReservationId(reservationIdToComplete)
            .orElseThrow(() -> ReservationNotFoundException.create(reservationIdToComplete));

        reservation.getReservationTools().forEach(reservationTool -> toolService.makeToolAvailableAndSave(reservationTool.getTool()));
        reservation.completeReservation();
        reservationDAO.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<Long> getReservationIdsForCompletion() {
        final LocalDateTime now = LocalDateTime.now();
        return reservationDAO.getReservationIdsWithDateToOlderThanAndStatus(now, ReservationStatus.ACTIVE);
    }
}
