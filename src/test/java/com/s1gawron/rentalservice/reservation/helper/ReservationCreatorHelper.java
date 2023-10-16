package com.s1gawron.rentalservice.reservation.helper;

import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.shared.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public enum ReservationCreatorHelper {

    I;

    public List<Reservation> createReservations() {
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();
        final List<ToolDetailsDTO> toolDetails = ToolCreatorHelper.I.createToolDTOList();

        final ReservationDetailsDTO firstReservationDetailsDTO = new ReservationDetailsDTO(1L, false, false, LocalDate.now(), LocalDate.now().plusDays(3L),
            BigDecimal.valueOf(10.99), "Hammer", List.of(toolDetails.get(0)));
        final Reservation firstReservation = Reservation.from(firstReservationDetailsDTO);
        firstReservation.addTool(tools.get(0));

        final ReservationDetailsDTO secondReservationDetailsDTO = new ReservationDetailsDTO(2L, false, false, LocalDate.now().plusDays(1L),
            LocalDate.now().plusDays(2L),
            BigDecimal.valueOf(1000.99), "Loader", List.of(toolDetails.get(1)));
        final Reservation secondReservation = Reservation.from(secondReservationDetailsDTO);
        secondReservation.addTool(tools.get(1));

        final ReservationDetailsDTO thirdReservationDetailsDTO = new ReservationDetailsDTO(3L, false, false, LocalDate.now().plusDays(2L),
            LocalDate.now().plusDays(4L),
            BigDecimal.valueOf(19999.99), "Crane", List.of(toolDetails.get(2)));
        final Reservation thirdReservation = Reservation.from(thirdReservationDetailsDTO);
        thirdReservation.addTool(tools.get(2));

        return List.of(firstReservation, secondReservation, thirdReservation);
    }

    public Reservation createReservation() {
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final ReservationDetailsDTO firstReservationDetailsDTO = new ReservationDetailsDTO(1L, false, false, LocalDate.now(), LocalDate.now().plusDays(3L),
            BigDecimal.valueOf(10.99), "Hammer", List.of(toolDetailsDTO));
        final Reservation firstReservation = Reservation.from(firstReservationDetailsDTO);

        firstReservation.addTool(ToolCreatorHelper.I.createTool());

        return firstReservation;
    }

    public Reservation createDifferentReservation() {
        final List<ToolDetailsDTO> toolDetailsDTOs = ToolCreatorHelper.I.createToolDTOList();
        final List<Tool> tools = ToolCreatorHelper.I.createToolList();
        final ReservationDetailsDTO firstReservationDetailsDTO = new ReservationDetailsDTO(1L, false, false, LocalDate.now(), LocalDate.now().plusDays(1L),
            BigDecimal.valueOf(1011.98), "Hammer and loader", List.of(toolDetailsDTOs.get(0), toolDetailsDTOs.get(1)));
        final Reservation firstReservation = Reservation.from(firstReservationDetailsDTO);

        firstReservation.addTool(tools.get(0));
        firstReservation.addTool(tools.get(1));

        return firstReservation;
    }

    public List<ReservationDetailsDTO> createReservationDetailsList() {
        final List<ToolDetailsDTO> toolDetails = ToolCreatorHelper.I.createToolDTOList();
        final ReservationDetailsDTO firstReservationDetailsDTO = new ReservationDetailsDTO(1L, false, false, LocalDate.now(), LocalDate.now().plusDays(3L),
            BigDecimal.valueOf(10.99), "Hammer", List.of(toolDetails.get(0)));
        final ReservationDetailsDTO secondReservationDetailsDTO = new ReservationDetailsDTO(2L, false, false, LocalDate.now().plusDays(1L),
            LocalDate.now().plusDays(2L),
            BigDecimal.valueOf(1000.99), "Loader", List.of(toolDetails.get(1)));
        final ReservationDetailsDTO thirdReservationDetailsDTO = new ReservationDetailsDTO(3L, false, false, LocalDate.now().plusDays(2L),
            LocalDate.now().plusDays(4L),
            BigDecimal.valueOf(19999.99), "Crane", List.of(toolDetails.get(2)));

        return List.of(firstReservationDetailsDTO, secondReservationDetailsDTO, thirdReservationDetailsDTO);
    }

    public ReservationDetailsDTO createReservationDetailsDTO() {
        return new ReservationDetailsDTO(1L, false, false, LocalDate.now(), LocalDate.now().plusDays(3L),
            BigDecimal.valueOf(10.99), "Hammer", List.of(ToolCreatorHelper.I.createToolDetailsDTO()));
    }

    public ReservationDetailsDTO createCanceledReservationDetailsDTO() {
        return new ReservationDetailsDTO(1L, false, true, LocalDate.now(), LocalDate.now().plusDays(3L),
            BigDecimal.valueOf(10.99), "Hammer", List.of(ToolCreatorHelper.I.createToolDetailsDTO()));
    }

    public Reservation createReservationForExpiry() {
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final ReservationDetailsDTO firstReservationDetailsDTO = new ReservationDetailsDTO(1L, false, false, LocalDate.now().minusDays(3L),
            LocalDate.now().minusDays(1L), BigDecimal.valueOf(10.99), "Hammer", List.of(toolDetailsDTO));
        final Reservation firstReservation = Reservation.from(firstReservationDetailsDTO);

        firstReservation.addTool(ToolCreatorHelper.I.createTool());

        return firstReservation;
    }

    public List<ReservationDetailsDTO> createReservationDetailsListWithFixedDate() {
        final List<ToolDetailsDTO> toolDetails = ToolCreatorHelper.I.createToolDTOList();
        final ReservationDetailsDTO firstReservationDetailsDTO = new ReservationDetailsDTO(1L, false, false, LocalDate.parse("2022-12-04"),
            LocalDate.parse("2022-12-16"), BigDecimal.valueOf(10.99), "Hammer", List.of(toolDetails.get(0)));
        final ReservationDetailsDTO secondReservationDetailsDTO = new ReservationDetailsDTO(2L, false, false, LocalDate.parse("2023-02-04"),
            LocalDate.parse("2023-03-16"), BigDecimal.valueOf(1000.99), "Loader", List.of(toolDetails.get(1)));
        final ReservationDetailsDTO thirdReservationDetailsDTO = new ReservationDetailsDTO(3L, false, false, LocalDate.parse("2023-01-01"),
            LocalDate.parse("2023-01-15"), BigDecimal.valueOf(19999.99), "Crane", List.of(toolDetails.get(2)));

        return List.of(firstReservationDetailsDTO, secondReservationDetailsDTO, thirdReservationDetailsDTO);
    }

}
