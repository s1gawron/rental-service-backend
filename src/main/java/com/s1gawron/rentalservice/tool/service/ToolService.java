package com.s1gawron.rentalservice.tool.service;

import com.s1gawron.rentalservice.reservationhastool.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.repository.ToolRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ToolService {

    private final ToolRepository toolRepository;

    @Transactional(readOnly = true)
    public List<ToolDTO> getToolsByReservations(final List<ReservationHasTool> reservations) {
        final List<ToolDTO> reservationsTools = new ArrayList<>();

        reservations.forEach(reservation -> {
            reservationsTools.add(ToolDTO.from(reservation.getTool(), reservation.getToolQuantity()));
        });

        return reservationsTools;
    }

    @Transactional
    public void saveTool(final Tool tool) {
        toolRepository.save(tool);
    }
}
